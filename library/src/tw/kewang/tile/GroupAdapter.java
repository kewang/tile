package tw.kewang.tile;

import java.util.ArrayList;

import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author kewang
 */
public class GroupAdapter extends PagerAdapter {
	private ArrayList<TileGroup> groups;
	private int current;
	private int deleting;
	private OnRemoveItemListener listener;
	private boolean instantiate;

	public GroupAdapter() {
		groups = new ArrayList<TileGroup>();

		setInstantiate(false);
	}

	public void addView(TileGroup v) {
		groups.add(v);

		notifyDataSetChanged();
	}

	public void removeView(int position) {
		Log.d("adapter", "beforeRemove_count" + getCount());

		deleting = position;

		if (listener != null) {
			listener.beforeRemove(getCount(), deleting, current);
		}

		groups.remove(deleting);

		notifyDataSetChanged();

		Log.d("adapter", "afterRemove_count" + getCount());
	}

	public void removeViewAtLast() {
		removeView(groups.size() - 1);
	}

	public void removeViewAtFirst() {
		removeView(0);
	}

	public ArrayList<TileGroup> getGroups() {
		return groups;
	}

	public boolean isInstantiate() {
		return instantiate;
	}

	public void setInstantiate(boolean instantiate) {
		this.instantiate = instantiate;
	}

	public void setOnRemoveItemListener(OnRemoveItemListener listener) {
		this.listener = listener;
	}

	@Override
	public int getCount() {
		return groups.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public void destroyItem(View container, int position, Object object) {
		Log.d("adapter", "destroyItem_position" + position);
		Log.d("adapter", "destroyItem_deleting" + deleting);
		Log.d("adapter", "destroyItem_views_size" + groups.size());

		if (position != groups.size()) {
			((Sliding) container).removeView(groups.get(position));
		}
	}

	@Override
	public Object instantiateItem(View container, int position) {
		TileGroup view = groups.get(position);

		Log.d("adapter", "instantiateItem_position" + position);
		Log.d("adapter", "instantiateItem_deleting" + deleting);
		Log.d("adapter", "instantiateItem_views_size" + groups.size());

		setInstantiate(true);

		((Sliding) container).addView(view);

		return view;
	}

	@Override
	public void setPrimaryItem(ViewGroup container, int position, Object object) {
		Log.d("adapter", "setPrimaryItem_position" + position);

		current = position;
	}

	public interface OnRemoveItemListener {
		public void beforeRemove(int size, int deletingIndex, int currentIndex);
	}
}