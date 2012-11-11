# About Tile
This is a **drag & drop** View for Android, you can feel free to use it. e.g. zoom-in, zoom-out, cell click, cell long-click...etc.

## Architecture

### `Tile`
can place any `TileGroup`s, `Sliding`s and built-in `View`s at Android.

#### Attributes
* `int holderSize`: Holder is a square area of `TileGroup` left/right side tightly. If `Tile` has `Sliding`s, can determine holder size to use it.

#### Methods
* `void addGroup(TileGroup group, int x, int y)`: add `TileGroup` to point `(x, y)`.
* `void addWidget(View widget, int x, int y)`: add `widget` to point `(x, y)`.
* `void removeGroup(TileGroup group)`: remove `TileGroup`.
* `void startDrag()`: start to drag `TileCell`.
* `void stopDrag()`: stop to drag `TileCell`.
* `ArrayList<TileGroup> getGroups()`: return an `ArrayList` of `TileGroup`s, excluding all `Sliding`s' `TileGroup`s.
* `ArrayList<TileGroup> getGroups(boolean includeSliding)`: return an `ArrayList` of `TileGroup`s, `includeSliding` determine if including all `Sliding`s' `TileGroup`s.
* `ArrayList<Sliding> getSlidings()`: return an `ArrayList` of `Sliding`s.
* `ArrayList<View> getWidgets()`: return an `ArrayList` of widgets.

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
* `Point location`: determine `TileGroup`'s position at `Tile`.
* `boolean canCellIn`: determine if `TileCell` can drag in.
* `boolean canCellOut`: determine if `TileCell` can drag out.
* `int maxCells`: determine the number of maximum `TileCell`s at `TileGroup`.

#### Methods
* `void addCell(TileCell cell, int index)`: add `TileCell` to position `index`.
* `void addCell(TileCell cell)`: add `TileCell` to last position.
* `void removeCell(TileCell cell)`: remove `TileCell`.
* `int getMaxCellSize()`: return `maxCells`.
* `void setMaxCellSize(int maxCells)`: determine the number of maximum `TileCell`s at `TileGroup`.
* `void addCellProperty(CellProperty property)`: add `CellProperty` to last position.
* `ArrayList<TileCell> getCells()`: return an `ArrayList` of `TileCell`s.
* `ArrayList<CellProperty> getCellProperties()`: return an `ArrayList` of `CellProperty`s.
* `int getCellPropertySize(boolean empty)`: return counts of all `CellProperty`, `empty` determine if `CellProperty` contains `TileCell`.
* `void addWidget(View widget, int x, int y)`: add `widget` to point `(x, y)`.
* `ArrayList<View> getWidgets()`: return an `ArrayList` of widgets.
* `void setSize(int width, int height)`: determine `TileGroup`'s `width` and `height`.
* `Point getLocation()`: return current location.
* `void setLocation(Point location)`: determine `TileGroup`'s current location at point `location`
* `void setLocation(int x, int y)`: determine `TileGroup`'s current location at point `(x, y)`
* `boolean canCellIn()`: return whether `TileGroup` can drag `TileCell` in.
* `void setCanCellIn(boolean canCellIn)`: determine if `TileCell` can drag in.
* `boolean canCellOut()`: return whether `TileGroup` can drag `TileCell` out.
* `void setCanCellOut(boolean canCellOut)`: determine if `TileCell` can drag out.
* `void startCellAnimation(int resId)`: start `TileCell`'s animation, `resId` determine the id of animation resource.
* `void stopCellAnimation()`: stop `TileCell`'s animation.
* `boolean hasSliding()`: return whether `TileGroup` can sliding.
* `CellProperty getNearestCellProperty(int x, int y)`: return the point `(x, y)` of nearest CellProperty.

### `TileGroup.CellProperty`
can describe `TileCell`'s width, height, and position.

#### Attributes
* `Bitmap background`: determine the CellProperty's `background`, if `cell` is empty.
* `TileGroup group`: determine the `cell`'s `TileGroup`.
* `TileCell cell`: determine the `CellProperty` contains `TileCell`'s instance.
* `Point location`: determine the `CellProperty`'s location.
* `int width`: determine the `CellProperty`'s width.
* `int height`: determine the `CellProperty`'s height.

### `TileCell`
is minimal element at this library, only can click & drag it.

#### Attributes
* `Point location`: determine `TileCell`'s position at `TileGroup`.
* `String name`: determine `TileCell`'s `name`.
#### Methods
* `void setSize(int width, int height)`: determine `TileCell`'s `width` and `height`.
* `Point getLocation()`: return current location.
* `void setLocation(Point location)`: determine `TileCell`'s location.
* `void setLocation(int x, int y)`:  determine `TileCell`'s location point `(x, y)`.
* `TileGroup getGroup()`: return `TileCell`'s parent `TileGroup`.
* `String getName()`: return `TileCell`'s `name`.
* `void setName(String name)`: determine `TileCell`'s `name`.

### `Sliding`
only can place `TileGroup`s to slide.

#### Attributes
* `int holderSize`: Holder is a square area of `TileGroup` left/right side tightly. If `Tile` has `Sliding`s, can determine holder size to use it.

#### Methods
* `void removeAtFirst()`: remove the first `TileGroup` of `Sliding`.
* `void removeAtLast()`: remove the last `TileGroup` of `Sliding`.
* `void add(TileGroup child)`: add `TileGroup` to `Sliding`.
* `void removeAt(int index)`: remove the `index + 1`-th `TileGroup` of `Sliding`.
* `int getHolderSize()`: return `holderSize`.
* `void setHolderSize(int holderSize)`: Holder is a square area of `TileGroup` left/right side tightly. If `Tile` has `Sliding`s, can determine holder size to use it.
* `void next()`: slide to the next `TileGroup`.
* `void previous()`: slide to the previous `TileGroup`.
* `ArrayList<TileGroup> getGroups()`:  return an `ArrayList` of `TileGroup`s.
* `TileGroup getGroup(int index)`: return the `index + 1`-th `TileGroup` of `Sliding`.
* `TileGroup getCurrentGroup()`: return the current `TileGroup`

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