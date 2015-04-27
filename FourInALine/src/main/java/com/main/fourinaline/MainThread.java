package com.main.fourinaline;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

/**
 * The game loop to render the game
 * 
 * @author Richard GUERCI & Julien SIERRA
 *
 */
public class MainThread extends Thread {

	private SurfaceHolder surfaceHolder;
	private GamePanel gamePanel;
	private boolean running;

	public MainThread(SurfaceHolder surfaceHolder, GamePanel gamePanel) {
		super();
		this.surfaceHolder = surfaceHolder;
		this.gamePanel = gamePanel;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	@Override
	public void run() {
		Canvas canvas;
		while (running) {
			canvas = null;
			try {
				canvas = this.surfaceHolder.lockCanvas();
				synchronized (surfaceHolder) {
					this.gamePanel.update();
					this.gamePanel.render(canvas);
				}
			} catch (Exception e) {
			} finally {

				if (canvas != null) {
					surfaceHolder.unlockCanvasAndPost(canvas);
				}
			}
		}
	}

}
