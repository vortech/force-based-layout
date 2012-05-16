/*  Copyright (c) 2010 Xiaoyun Zhu
 * 
 *  Permission is hereby granted, free of charge, to any person obtaining a copy  
 *  of this software and associated documentation files (the "Software"), to deal  
 *  in the Software without restriction, including without limitation the rights  
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell  
 *  copies of the Software, and to permit persons to whom the Software is  
 *  furnished to do so, subject to the following conditions:
 *  
 *  The above copyright notice and this permission notice shall be included in  
 *  all copies or substantial portions of the Software.
 *  
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR  
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,  
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE  
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER  
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,  
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN  
 *  THE SOFTWARE.  
 */
package cn.kk.forcedgraph.layout;

import java.util.concurrent.TimeUnit;

import cn.kk.forcedgraph.graph.BoundingBox;
import cn.kk.forcedgraph.listener.GraphFinishedListener;
import cn.kk.forcedgraph.listener.LayoutRenderListener;

public class GraphRenderer implements Runnable {
    private static final double POSITION_MOD_PERCENT = 0.03;
    private static final double VELOCITY_MOD_PERCENT = 0.03;

    private final ForceDirectedLayout layout;
    private long interval;
    private LayoutRenderListener rendererListener;
    private GraphFinishedListener finishedListener;
    private BoundingBox currentBB;
    private boolean rendering = false;

    public GraphRenderer(final ForceDirectedLayout layout) {
        this.layout = layout;
    }

    public GraphRenderer(final ForceDirectedLayout layout, final long interval,
            final LayoutRenderListener rendererListener, final GraphFinishedListener finishedListener,
            final BoundingBox currentBB) {
        this.layout = layout;
        this.interval = interval;
        this.rendererListener = rendererListener;
        this.finishedListener = finishedListener;
        this.currentBB = currentBB;
    }

    private void adjustBB() {
        final BoundingBox targetBB = this.layout.getBoundingBox();
        // current gets 20% closer to target every iteration
        this.currentBB.getBottomLeft().add(targetBB.getBottomLeft().substract(this.currentBB.getBottomLeft())
                .divide(10));
        this.currentBB.getTopRight().add(targetBB.getTopRight().substract(this.currentBB.getTopRight())
                .divide(10));
    }

    public final boolean isRendering() {
        return this.rendering;
    }

    @Override
    public void run() {
        synchronized (this.layout) {
            if (this.rendering) {
                if (update()) {
                    this.rendering = false;
                } else {
                    GraphPanel.SCHEDULER.schedule(this, this.interval, TimeUnit.MILLISECONDS);
                }
            }
        }
    }

    public void start(final long interval,
            final LayoutRenderListener rendererListener, final GraphFinishedListener finishedListener,
            final BoundingBox currentBB) {
        this.interval = interval;
        this.rendererListener = rendererListener;
        this.finishedListener = finishedListener;
        this.currentBB = currentBB;
        if (!this.rendering) {
            this.rendering = true;
            run();
        }
    }

    public void stop() {
        this.rendering = false;
    }

    /**
     * 
     * @return finished flag
     */
    private final boolean update() {
        this.layout.applyCoulombsLaw();
        this.layout.applyHookesLaw();
        this.layout.attractToCentre();
        this.layout.updateVelocity(VELOCITY_MOD_PERCENT);
        this.layout.updatePosition(POSITION_MOD_PERCENT);

        if (this.rendererListener != null) {
            this.rendererListener.onRender();
        }

        // stop simulation when energy of the system goes below a threshold
        if (this.layout.getTotalEnergy() < 0.01) {
            this.layout.stopRendering();
            if (this.finishedListener != null) {
                this.finishedListener.onFinished();
            }
            this.currentBB.update(this.layout.getBoundingBox());
            return true;
        } else {
            adjustBB();
            return false;
        }
    }

}
