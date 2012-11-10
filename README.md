# About Tile
This is a **drag & drop** View for Android, you can feel free to use it. e.g. zoom-in, zoom-out, cell click, cell long-click...etc.

## Architecture

### `Tile`
can place any `TileGroup`s, `Sliding`s and built-in `View`s at Android.

#### Attributes
* `holderSize`: Holder is a square area of `TileGroup` left/right side tightly. If `Tile` has `Sliding`s, can determine holder size to use it.

#### Methods
* `void addGroup(TileGroup group, int x, int y)`: add `TileGroup` to point `(x, y)`.
* `void addWidget(View widget, int x, int y)`: add `View` to point `(x, y)`.
* `void removeGroup(TileGroup group)`: remove `TileGroup`.
* `void startDrag()`: start to drag `TileCell`
* `void stopDrag()`: stop to drag `TileCell`
* `ArrayList<TileGroup> getGroups()`: return all `TileGroup`s of `ArrayList`, excluding `Sliding`'s `TileGroup`s
* `ArrayList<TileGroup> getGroups(boolean includeSliding)`: return all `TileGroup`s of `ArrayList`, `includeSliding` determine if including `Sliding`'s `TileGroup`s
* `ArrayList<Sliding> getSlidings()`: return all `Sliding`s of `ArrayList`
* `ArrayList<View> getWidgets()`: return all `View`s of `ArrayList`

#### Callbacks

##### `OnDragListener`

* `setOnCellDragListener(OnDragListener listener)`:
 	* `void onDragStart(TileGroup group, TileCell cell)`
	* `void onDragStop(TileGroup group, TileCell cell, boolean animation)`

##### `OnLongClickListener`

* `setOnCellLongClickListener(OnLongClickListener listener)`:
	* `void onLongClick(TileGroup group, TileCell cell)`

##### `OnClickListener`

* `setOnCellClickListener(OnClickListener listener)`:
	* `void onClick(TileGroup group, TileCell cell)`

### `TileGroup`
can place any `TileCell`s and built-in `View`s at Android.

#### Attributes
* `location`:
* `canCellIn`:
* `canCellOut`:
* `maxCells`:

#### Methods

### `TileGroup.CellProperty`
can describe `TileCell`'s width, height, and position.

### `TileCell`
is minimal element at this library, only can click & drag it.
#### Attributes
#### Methods

### `Sliding`
only can place `TileGroup`s to slide.
#### Attributes
#### Methods

## How-to use

### Part 1
#### Layout
#### Java

### Part 2
#### Layout
#### Java

### Part 3
#### Layout
#### Java