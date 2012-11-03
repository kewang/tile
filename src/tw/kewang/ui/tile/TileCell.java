package tw.kewang.ui.tile;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import tw.kewang.ui.tile.R;

/**
 * @author kewang
 */
public class TileCell extends ImageView {
	private Point location;
	private String name;

	public TileCell(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		init(context, attrs);
	}

	public TileCell(Context context, AttributeSet attrs) {
		super(context, attrs);

		init(context, attrs);
	}

	public TileCell(Context context) {
		super(context);

		init(context, null);
	}

	private void init(Context context, AttributeSet attrs) {
		if (attrs != null) {
			TypedArray a = context.obtainStyledAttributes(attrs,
					R.styleable.TileAttrs);

			location = new Point((int) a.getDimension(R.styleable.TileAttrs_x,
					0), (int) a.getDimension(R.styleable.TileAttrs_y, 0));

			a.recycle();
		} else {
			location = new Point();
		}
	}

	/**
	 * 設定Cell的寬高
	 * 
	 * @param width
	 * @param height
	 */
	public void setSize(int width, int height) {
		if (getLayoutParams() == null) {
			setLayoutParams(new FrameLayout.LayoutParams(width, height));
		} else {
			getLayoutParams().width = width;
			getLayoutParams().height = height;
		}
	}

	/**
	 * 取得Cell在Group的位置
	 * 
	 * @return
	 */
	public Point getLocation() {
		return location;
	}

	/**
	 * 設定Cell的位置
	 * 
	 * @param location
	 */
	public void setLocation(Point location) {
		setLocation(location.x, location.y);
	}

	/**
	 * 設定Cell的位置
	 * 
	 * @param x
	 * @param y
	 */
	public void setLocation(int x, int y) {
		location.set(x, y);
	}

	/**
	 * 取得目前的Group
	 * 
	 * @return
	 */
	public TileGroup getGroup() {
		return (TileGroup) getParent();
	}

	/**
	 * 取得Name
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * 設定Name
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}
}