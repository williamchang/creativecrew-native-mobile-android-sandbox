/**
@file
    TileView.java
@brief
    Coming soon.
@author
    William Chang
@version
    0.1
@date
    - Created: 2007-11-22
    - Modified: 2007-11-26
    .
@note
    References:
    - General:
        - http://tilestudio.sourceforge.net/
        - http://en.wikipedia.org/wiki/Java_syntax
        - http://code.google.com/android/reference/android/graphics/drawable/Drawable.html
        - http://code.google.com/android/reference/android/graphics/Bitmap.html
        - http://code.google.com/android/reference/android/graphics/Canvas.html
        .
    .
*/

package diehard.sandbox;

import java.util.ArrayList;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Class TileView.
 * @author William Chang
 */
public class TileView extends View {
    /** Argument constructor. Constructs based on inflation from XML. */
    public TileView(Context context, AttributeSet attrs, Map inflateParams) {
        // Mandatory call to the super class.
        super(context, attrs, inflateParams);
        // Instantiate this.
        if(!instantiateThis()) return;
    }
    /** Argument constructor. */
    public TileView(Context context, AttributeSet attrs, Map inflateParams, int defStyle) {
        // Mandatory call to the super class.
        super(context, attrs, inflateParams, defStyle);
        // Instantiate this.
        if(!instantiateThis()) return;
    }
    /** Instantiate this. */
    public boolean instantiateThis() {
        _paint = new Paint();
        _entityList = new ArrayList<Entity>();
        _viewWidth = 0;
        _viewHeight = 0;

        return true;
    }
    /** Event on draw */
    @Override
    protected void onDraw(Canvas canvas) {
        // Mandatory call to the super class.
        super.onDraw(canvas);
        // Draw tiles onto canvas.
         for(int x=0;x<_entityMain.getMapWidth();x++) {
            for(int y=0;y<_entityMain.getMapHeight();y++) {
                if(_entityMain.getMapTile(y, x) > 0) {
                    canvas.drawBitmap(_entityMain.getTileBitmap(y, x), null, getViewRectangle(x, y, _viewOffsetX, _viewOffsetY, _entityMain), _paint);
                }
            }
         }
         for(Entity e:_entityList) {
             if(e.getType() == Entity.ITEM) {
                 int id = 0;
                 int x = e.getMapSequenceList()[id].getPositionX();
                 int y = e.getMapSequenceList()[id].getPositionY();
                 canvas.drawBitmap(e.getFrameBitmap(id), null, getViewRectangle(x, y, _viewOffsetX, _viewOffsetY, e), _paint);
             }
         }
    }
    /** Event on size changed. */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        _viewWidth = w;
        _viewHeight = h;
        _viewTilePerX = (int)Math.floor(w / _entityMain.getTileWidth());
        _viewTilePerY = (int)Math.floor(h / _entityMain.getTileHeight());
        _viewOffsetX = ((w - (_entityMain.getTileWidth() * _viewTilePerX)) / 2);
        _viewOffsetY = ((h - (_entityMain.getTileHeight() * _viewTilePerY)) / 2);
    }
    /** Get tile set. */
    public Bitmap getTileSetBitmap(Drawable tileSet, int tileSetWidth, int tileSetHeight) {
        Bitmap b = Bitmap.createBitmap(tileSetWidth, tileSetHeight, true);
        Canvas c = new Canvas(b);
        tileSet.setBounds(0, 0, tileSetWidth, tileSetHeight);
        tileSet.draw(c);
        return b;
    }
    /**
     * Get tile.
     * @param tileSet The tile set.
     * @param tileId The identity of a tile from the tile set.
     * */
    public Bitmap getTileBitmap(Bitmap tileSet, int tileId, int tileWidth, int tileHeight, int tilePerCol) {
        int col = 0;
        int row = 0;

        tileId--;
        col = tileId % tilePerCol;
        row = tileId / tilePerCol;
        return Bitmap.createBitmap(tileSet, col * tileWidth, row * tileHeight, tileWidth, tileHeight);
    }
    /** Parse tile set into tiles. */
    public Bitmap[] parseTileSet(Drawable tileSet, int tileSetWidth, int tileSetHeight, int tileWidth, int tileHeight) {
        int tilePerCol = (int)Math.floor(tileSetWidth / tileWidth);
        int tilePerRow = (int)Math.floor(tileSetHeight / tileHeight);
        int tileTotal = tilePerCol * tilePerRow;
        Bitmap b = getTileSetBitmap(tileSet, tileSetWidth, tileSetHeight);
        Bitmap[] a = new Bitmap[tileTotal + 1];
        for(int i=1;i<=tileTotal;i++) {
            a[i] = getTileBitmap(b, i, tileWidth, tileHeight, tilePerCol);
        }
        // If empty or null, then must use an empty (transparent) tile.
        a[0] = getTileBitmap(b, tileTotal, tileWidth, tileHeight, tilePerCol);
        return a;
    }
    /**
     * Get rectangle coordinates to crop tile relative to tile set.
     * @param tileId The identity of a tile from the tile set.
     * */
    public Rect getTileRectangle(int tileId, int tileWidth, int tileHeight, int tilePerCol) {
        int col = 0;
        int row = 0;

        tileId--;
        col = tileId % tilePerCol;
        row = tileId / tilePerCol;

        int left = col * tileWidth;
        int top = row * tileHeight;
        int right = left + tileWidth;
        int bottom = top + tileHeight;

        return new Rect(left, top, right, bottom);
    }
    /** Get rectangle coordinates to set tile relative to view. */
    public Rect getViewRectangle(int x, int y, int offsetX, int offsetY, Entity e) {
        int left = offsetX + x * e.getTileWidth();
        int top = offsetY + y * e.getTileHeight();
        int right = left + e.getTileWidth();
        int bottom = top + e.getTileHeight();

        return new Rect(left, top, right, bottom);
    }
    /** Get offset x. */
    public int getOffsetX() {
        return _viewOffsetX;
    }
    /** Get offset y. */
    public int getOffsetY() {
        return _viewOffsetY;
    }
    /** Is collision. */
    public boolean isCollision(int x, int y, Entity e) {
        int nearTopTileX = (int)Math.floor(x / _entityMain.getTileWidth());
        int nearLeftTileY = (int)Math.floor(y / _entityMain.getTileHeight());
        int nearRightTileX = (int)Math.ceil(x / _entityMain.getTileWidth());
        int nearBottomTileY = (int)Math.ceil(y / _entityMain.getTileHeight());

        if(x > _entityMain.getTileSetWidth() || y > _entityMain.getTileSetHeight()) {
            return true;
        } else if(x < 0 || y < 0) {
            return true;
        } else if((_entityMain.getMapBound(nearRightTileX, nearBottomTileY) & FLAG_BIT_2) != 0) {
            return true;
        }

        return false;
    }
    /** Set main entity. */
    public void setEntityMain(Entity entity) {
        _entityMain = entity;
    }
    /** Set view offset coordinates. */
    public void setViewOffset(int x, int y) {
        _viewOffsetX = x;
        _viewOffsetY = y;
    }
    /** Add entity. */
    public void addEntity(Entity entity) {
        _entityList.add(entity);
    }

    private int _viewWidth;
    private int _viewHeight;
    private int _viewTilePerX;
    private int _viewTilePerY;
    private int _viewOffsetX;
    private int _viewOffsetY;

    private Entity _entityMain;
    private ArrayList<Entity> _entityList = new ArrayList<Entity>();
    private Paint _paint;

    // Bit flags of conditions.
    private int FLAG_BIT_0 = 1; // Upper bound.
    private int FLAG_BIT_1 = 2; // Left bound.
    private int FLAG_BIT_2 = 4; // Lower bound.
    private int FLAG_BIT_3 = 8; // Right bound.
    private int FLAG_BIT_4 = 16;
    private int FLAG_BIT_5 = 32;
    private int FLAG_BIT_6 = 64;
    private int FLAG_BIT_7 = 128; // Non-diagonal bounds.

    /** Class Entity. */
    class Entity {
        /** Argument constructor. */
        public Entity(String name, int type) {
            _name = name;
            _type = type;
            // Instantiate this.
            if(!instantiateThis()) return;
        }
        /** Instantiate this. */
        public boolean instantiateThis() {
            _tileSetWidth = 0;
            _tileSetHeight = 0;
            _tileWidth = 0;
            _tileHeight = 0;
            _mapWidth = 0;
            _mapHeight = 0;
            _mapSequenceListTotal = 0;

            return true;
        }
        /** Get name. */
        public String getName() {
            return _name;
        }
        /** Get type. */
        public int getType() {
            return _type;
        }
        /** Get tile width. */
        public int getTileWidth() {
            return _tileWidth;
        }
        /** Get tile height. */
        public int getTileHeight() {
            return _tileHeight;
        }
        /** Get tile bitmap by tileId. */
        public Bitmap getTileBitmap(int tileId) {
            return _tileBitmaps[tileId];
        }
        /** Get tile bitmap by the map's dimensions. */
        public Bitmap getTileBitmap(int dimenson1, int dimenson2) {
            return _tileBitmaps[_mapTile[dimenson1][dimenson2]];
        }
        /** Get tile set width. */
        public int getTileSetWidth() {
            return _tileSetWidth;
        }
        /** Get tile set height. */
        public int getTileSetHeight() {
            return _tileSetHeight;
        }
        /** Get map tile. */
        public int[][] getMapTile() {
            return _mapTile;
        }
        /** Get map tile by dimensions. */
        public int getMapTile(int dimenson1, int dimenson2) {
            return _mapTile[dimenson1][dimenson2];
        }
        /** Get map tile. */
        public int[][] getMapBoundAndCode() {
            return _mapBoundAndCode;
        }
        /** Get map bound and code by dimensions. */
        public int getMapBoundAndCode(int dimenson1, int dimenson2) {
            return _mapBoundAndCode[dimenson1][dimenson2];
        }
        /** Get map sequences list. */
        public EntityAnimation[] getMapSequenceList() {
            return _mapSequenceList;
        }
        /** Get map sequences list total. */
        public int getMapSequenceListTotal() {
            return _mapSequenceListTotal;
        }
        /** Get frame bitmap. */
        public Bitmap getFrameBitmap(int mapSequenceId) {
            int tileId;
            int frameCurrent = _mapSequenceList[mapSequenceId].getFrameCurrent();
            int frameTotal = _mapSequenceList[mapSequenceId].getFrameTotal();
            if(frameCurrent < frameTotal) {
                tileId = _mapSequenceList[mapSequenceId].getTileId();
                if(!_mapSequenceList[mapSequenceId].isDelayed()) {
                    _mapSequenceList[mapSequenceId].setFrameCurrent(frameCurrent + 1);
                }
            } else if(_mapSequenceList[mapSequenceId].isRepeat()) {
                _mapSequenceList[mapSequenceId].setFrameCurrent(0);
                tileId = getMapSequenceList()[mapSequenceId].getTileId();
            } else {
                tileId = getMapSequenceList()[mapSequenceId].getTileId(frameTotal - 1);
            }
            return _tileBitmaps[tileId];
        }
        /** Get map width. */
        public int getMapWidth() {
            return _mapWidth;
        }
        /** Get map height. */
        public int getMapHeight() {
            return _mapHeight;
        }
        /** Get map bound. */
        public int getMapBound(int x, int y) {
            return _mapBoundAndCode[y][x] & 0x00FF;
        }
        /** Get map code. */
        public int getMapCode(int x, int y) {
            return (_mapBoundAndCode[y][x] & 0xFF00) >> 8;
        }
        /**
         * Set tile set.
         * @param tileSet The drawable tile set of an image.
         * @param tileWidth The width of each tile.
         * @param tileHeight The height of each tile.
         * */
        public void setTileSet(Drawable tileSet, int tileWidth, int tileHeight) {
            _tileSetWidth = tileSet.getIntrinsicWidth();
            _tileSetHeight = tileSet.getIntrinsicHeight();
            _tileWidth = tileWidth;
            _tileHeight = tileHeight;
            _tileBitmaps = parseTileSet(tileSet, _tileSetWidth, _tileSetHeight, tileWidth, tileHeight);
        }
        /** Set map tile. */
        public void setMapTile(int[][] array, int dimenson1, int dimenson2) {
            _mapTile = array;
            _mapWidth = dimenson2;
            _mapHeight = dimenson1;
            _mapSequenceList = new EntityAnimation[_mapHeight];
        }
        /** Set map bound and code. */
        public void setMapBoundAndCode(int[][] array) {
            _mapBoundAndCode = array;
        }
        /** Add map sequence. */
        public void addMapSequence(EntityAnimation singleSequence) {
            if(_mapSequenceListTotal < _mapHeight) {
                _mapSequenceList[_mapSequenceListTotal] = singleSequence;
                _mapSequenceListTotal++;
            } else {
                return;
            }
        }

        /// Name.
        private String _name;
        /// Type.
        private int _type;
        /// Tile set width.
        private int _tileSetWidth;
        /// Tile set height.
        private int _tileSetHeight;
        /// Tile width.
        private int _tileWidth;
        /// Tile height.
        private int _tileHeight;
        /// Tile bitmaps.
        private Bitmap[] _tileBitmaps;
        /// Map width.
        private int _mapWidth;
        /// Map height.
        private int _mapHeight;
        /// Total sequences.
        private int _mapSequenceListTotal;
        /// Map sequence, snimated sprites (Tile, FrameCount, Code).
        private EntityAnimation[] _mapSequenceList;
        /// Map for tiles. [height][width] [y][x]
        private int[][] _mapTile;
        /// Map for bounds (collision) and map codes (items, hexadecimal from 01 to FF). [height][width] [y][x]
        private int[][] _mapBoundAndCode;

        // Enum: Type
        public static final int STAGE = 0;
        public static final int ITEM = 1;
    };
    /** Class EntityAnimation. */
    class EntityAnimation {
        /** Argument constructor. */
        public EntityAnimation(int id, int[] animation, int dimension) {
            _id = id;
            _dimension = dimension;
            _sequence = animation;
            _isRepeat = true;
            _frameCurrent = 0;
            _frameDelayCurrent = getFrameDelay();
        }
        /** Get id. */
        public int getId() {
            return _id;
        }
        /** Get position x. */
        public int getPositionX() {
            return _positionX;
        }
        /** Get position y. */
        public int getPositionY() {
            return _positionY;
        }
        /** Get current frame. */
        public int getFrameCurrent() {
            return _frameCurrent;
        }
        /** Get delay frame by current frame. */
        public int getFrameDelay() {
            return _sequence[(_frameCurrent * 3) + 1];
        }
        /** Get total frames. */
        public int getFrameTotal() {
            return _dimension / 3;
        }
        /** Get array length. */
        public int getLength() {
            return _dimension;
        }
        /** Get tile id by current frame. */
        public int getTileId() {
            return _sequence[_frameCurrent * 3];
        }
        /** Get tile id. */
        public int getTileId(int frame) {
            return _sequence[frame * 3];
        }
        /** Get map code by current frame. */
        public int getMapCode() {
            return _sequence[(_frameCurrent * 3) + 2];
        }
        /** Get sequence (animation set). */
        public int[] getSequence() {
            return _sequence;
        }
        /** Is delayed. */
        public boolean isDelayed() {
            if(_frameDelayCurrent > 0) {
                _frameDelayCurrent--;
                return true;
            } else {
                return false;
            }
        }
        /** Is repeat. */
        public boolean isRepeat() {
            return _isRepeat;
        }
        /** Set repeat. */
        public void setRepeat(boolean isRepeat) {
            _isRepeat = isRepeat;
        }
        /** Set position x and y. */
        public void setPositions(int x, int y) {
            _positionX = x;
            _positionY = y;
        }
        /** Set current frame. */
        public void setFrameCurrent(int current) {
            _frameCurrent = current;
            if(_frameCurrent < getFrameTotal()) {
                _frameDelayCurrent = getFrameDelay();
            }
        }

        /// Name.
        private int _id;
        /// Repeat.
        private boolean _isRepeat;
        /// Position x.
        private int _positionX;
        /// Position y.
        private int _positionY;
        /// Current frame.
        private int _frameCurrent;
        /// Current delay frame.
        private int _frameDelayCurrent;
        /// Array length.
        private int _dimension;
        /// Sequence (animation set).
        private int[] _sequence;
    };
}