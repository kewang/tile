package tw.kewang.tile;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

/**
 * @author kewang
 */
public class ViewAnimation {
	public static void run(Animation animation, final View v, int duration,
			final Runner runner) {
		animation.setDuration(duration);

		animation.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				v.clearAnimation();

				if (runner != null) {
					runner.onAnimationEnd(v);
				}
			}
		});

		v.startAnimation(animation);
	}

	public interface Runner {
		public void onAnimationEnd(View v);
	}
}