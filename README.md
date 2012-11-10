# About Tile
This is a **drag & drop** View for Android, you can feel free to use it. e.g. zoom-in, zoom-out, cell click, cell long-click...etc.

## Architecture
* `Tile` class can place any `TileGroup`s and built-in `View`s at Android.
* `TileGroup` class can place any `TileCell`s and built-in `View`s at Android.
* `TileCell` class is minimal element at this library, only can click & drag it.
* `Sliding` class only can place `TileGroup`s to slide.

## How-to use
### Layout XML
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tile="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent" >

    <tw.kewang.ui.tile.Tile
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:layout_gravity="right"
        android:background="#888888" >

        <tw.kewang.ui.tile.TileGroup
            android:layout_width="300dp"
            android:layout_height="400dp"
            android:background="#00ffff"
            tile:maxCells="3"
            tile:x="450dp"
            tile:y="50dp" >

            <tw.kewang.ui.tile.TileCell
                android:layout_width="100dp"
                android:layout_height="100dp"
                android:background="#ff0000"
                tile:x="50dp"
                tile:y="50dp" />

            <tw.kewang.ui.tile.TileCell
                android:layout_width="50dp"
                android:layout_height="50dp"
                android:background="#00ff00"
                tile:x="100dp"
                tile:y="150dp" />

            <tw.kewang.ui.tile.TileCell
                android:layout_width="150dp"
                android:layout_height="150dp"
                android:background="#0000ff"
                tile:x="150dp"
                tile:y="150dp" />
        </tw.kewang.ui.tile.TileGroup>

        <Button
            android:id="@+id/button_outer"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_marginLeft="50dp"
            android:layout_marginTop="50dp"
            android:text="Outer Group" />

        <tw.kewang.ui.tile.TileGroup
            android:layout_width="300dp"
            android:layout_height="400dp"
            android:background="#ffff00"
            tile:maxCells="3"
            tile:x="850dp"
            tile:y="50dp" >

            <tw.kewang.ui.tile.TileCell
                android:layout_width="100dp"
                android:layout_height="100dp"
                android:background="#336699"
                tile:x="0dp"
                tile:y="0dp" />

            <tw.kewang.ui.tile.TileCell
                android:layout_width="50dp"
                android:layout_height="50dp"
                android:background="#663399"
                tile:x="100dp"
                tile:y="50dp" />

            <tw.kewang.ui.tile.TileCell
                android:layout_width="150dp"
                android:layout_height="150dp"
                android:background="#669933"
                tile:x="0dp"
                tile:y="150dp" />

            <Button
                android:id="@+id/button_inner"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:layout_marginLeft="50dp"
                android:layout_marginTop="325dp"
                android:text="Inner Group" />
        </tw.kewang.ui.tile.TileGroup>
    </tw.kewang.ui.tile.Tile>

</LinearLayout>
### Java code
#### Part 1
	tile2 = (Tile) findViewById(R.id.tile_2);
	group1 = (TileGroup) findViewById(R.id.group_1);
	s1 = (Sliding) findViewById(R.id.s1);

#### Part 2
	tile2.setOnCellLongClickListener(new Tile.OnLongClickListener() {
		@Override
		public void onLongClick(TileGroup group, TileCell cell) {
			tile2.startDrag();
		}
	});

#### Part 3
	CellProperty property = new TileGroup.CellProperty();

	property.width = 50;
	property.height = 50;
	property.location = new Point(50 * i, 50 * i++);

	group1.addCellProperty(property);

#### Part 4
	group1.addCell(getCell());

	private TileCell getCell() {
		TileCell cell = new TileCell(this);

		cell.setBackgroundColor(Color.rgb((int) (Math.random() * 0xff),
				(int) (Math.random() * 0xff), (int) (Math.random() * 0xff)));

		return cell;
	}