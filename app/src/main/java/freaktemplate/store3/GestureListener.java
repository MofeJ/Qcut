package freaktemplate.store3;

import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;

class GestureListener extends SimpleOnGestureListener {

	@Override
    public boolean onDown(MotionEvent e) {
        return true;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
            float velocityY) {
        return false;
    }

}
