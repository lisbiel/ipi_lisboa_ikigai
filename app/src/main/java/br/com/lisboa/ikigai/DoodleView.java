package br.com.lisboa.ikigai;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Region;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class DoodleView extends View {

    private DatabaseReference mDatabase;

    private static final int TOUCH_TOLERANCE = 10;

    private int RADIUS;

    private static final int ALPHA = 255;

    private static final int AUTO_VALUE = 13;

    private Paint paintLine;
    private Bitmap bitmap;
    private Canvas canvasBitmap;
    private Paint paintScreen;

    private Region region;

    private Region region2;

    private Region region3;

    private Region region4;
    Region r1;
    Region r2;
    Region r3;
    Region r4;

    private Map<Integer, Path> pathMap = new HashMap<>();

    private  Map<Integer, Point> previousPointMap = new HashMap<>();

    private boolean regionD;

    private boolean regionD2;

    private boolean regionD3;

    private boolean regionD4;

    FragmentManager fragmentManager;

    ArrayList<String> added;
    ArrayList<String> love;
    ArrayList<String> good;
    ArrayList<String> need;
    ArrayList<String> paid;
    ArrayList<String> passion;
    ArrayList<String> mission;
    ArrayList<String> profession;
    ArrayList<String> vocation;

    String typing;

    ArrayList<String> sugestions;

    public DoodleView(Context context, AttributeSet set) {
        super(context, set);
        paintScreen = new Paint();
        paintLine = new Paint();
        paintLine.setAntiAlias(true);
        paintLine.setColor(Color.BLACK);
        paintLine.setStyle(Paint.Style.STROKE);
        paintLine.setStrokeWidth(5);
        paintLine.setStrokeCap(Paint.Cap.ROUND);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        final Activity activity = (Activity) context;
        fragmentManager = activity.getFragmentManager();
        added = new ArrayList<String >();
        love = new ArrayList<String >();
        mission = new ArrayList<String >();
        vocation = new ArrayList<String >();
        profession = new ArrayList<String >();
        good = new ArrayList<String >();
        need = new ArrayList<String >();
        paid = new ArrayList<String >();
        passion = new ArrayList<String >();
        sugestions = new ArrayList<String >();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        canvasBitmap = new Canvas(bitmap);
        bitmap.eraseColor(Color.WHITE);
        canvasBitmap.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        regionD = false;
        regionD2 = false;
        regionD3 = false;
        regionD4 = false;
        RADIUS = (int) (getWidth()* 0.3);
    }

    public void clear()  {
        pathMap.clear();
        previousPointMap.clear();
        bitmap.eraseColor(Color.WHITE);
        invalidate();
    }

    public void setDrawingColor (int color) {
        this.paintLine.setColor(color);
    }

    public int getDrawingColor () {
        return this.paintLine.getColor();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(bitmap, 0, 0, paintScreen);
        //int A = (int)(Math.random()*256);
        for (Integer key: pathMap.keySet()) {
            canvas.drawPath(pathMap.get(key), paintLine);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();

        int actionIndex = event.getActionIndex();

        if(!regionD4) {
            if( action == MotionEvent.ACTION_DOWN ||
                    action == MotionEvent.ACTION_POINTER_DOWN) {
                touchStarted(
                        event.getX(actionIndex),
                        event.getY(actionIndex),
                        event.getPointerId(actionIndex)
                );

            } else if(action == MotionEvent.ACTION_UP ||
                    action == MotionEvent.ACTION_POINTER_UP) {
                touchEnded (event.getPointerId(actionIndex));
            } else {
                touchMoved(event);
            }
            invalidate();
        } else {
            int x = (int) event.getX();
            int y = (int) event.getY();
            /*sugestions.clear();
            sugestions = new ArrayList<String>();*/
            boolean area1 = region.contains(x, y);
            boolean area2 = region2.contains(x, y);
            boolean area3 = region3.contains(x, y);
            boolean area4 = region4.contains(x, y);
            InsertFragment insertFragment =
                    new InsertFragment();
            if(area1 && area2 && area3 && area4) {
                typing = "ikigai";
                insertFragment.setTyping(typing);
                insertFragment.setArray(added);
                insertFragment.setInserting(getContext().getString(R.string.ikigai));
                insertFragment.show(((AppCompatActivity) getContext()).getSupportFragmentManager(), "insert");
            }
            else if(area1 && !area2 && !area3 && !area4) {
                typing = "love";
                insertFragment.setTyping(typing);
                insertFragment.setArray(love);
                insertFragment.setInserting(getContext().getString(R.string.love));
                insertFragment.show(((AppCompatActivity) getContext()).getSupportFragmentManager(), "insert");
            } else if(!area1 && area2 && !area3 && !area4) {
                typing = "good";
                insertFragment.setTyping(typing);
                insertFragment.setArray(good);
                insertFragment.setInserting(getContext().getString(R.string.good));
                insertFragment.show(((AppCompatActivity) getContext()).getSupportFragmentManager(), "insert");
            } else if(!area1 && !area2 && area3 && !area4) {
                typing = "need";
                insertFragment.setTyping(typing);
                insertFragment.setArray(need);
                insertFragment.setInserting(getContext().getString(R.string.need));
                insertFragment.show(((AppCompatActivity) getContext()).getSupportFragmentManager(), "insert");
            } else if(area1 && area2 && !area3 && !area4) {
                typing = "passion";
                insertFragment.setTyping(typing);
                insertFragment.setArray(passion);
                insertFragment.setInserting(getContext().getString(R.string.passion));
                insertFragment.show(((AppCompatActivity) getContext()).getSupportFragmentManager(), "insert");
            } else if(area1 && !area2 && area3 && !area4) {
                typing = "mission";
                insertFragment.setTyping(typing);
                insertFragment.setArray(mission);
                insertFragment.setInserting(getContext().getString(R.string.mission));
                insertFragment.show(((AppCompatActivity) getContext()).getSupportFragmentManager(), "insert");
            } else if(!area1 && !area2 && area3 && area4) {
                typing = "vocation";
                insertFragment.setTyping(typing);
                insertFragment.setArray(vocation);
                insertFragment.setInserting(getContext().getString(R.string.vocation));
                insertFragment.show(((AppCompatActivity) getContext()).getSupportFragmentManager(), "insert");
            } else if(!area1 && area2 && !area3 && area4) {
                typing = "profession";
                insertFragment.setTyping(typing);
                insertFragment.setArray(profession);
                insertFragment.setInserting(getContext().getString(R.string.profession));
                insertFragment.show(((AppCompatActivity) getContext()).getSupportFragmentManager(), "insert");
            } else if(!area1 && !area2 && !area3 && area4) {
                typing = "paid";
                insertFragment.setTyping(typing);
                insertFragment.setArray(paid);
                insertFragment.setInserting(getContext().getString(R.string.paid));
                insertFragment.show(((AppCompatActivity) getContext()).getSupportFragmentManager(), "insert");
            }
            return false;
        }
        return true;
    }

    private void checkIntersection() {
        if ((!r1.quickReject(r2) && r1.op(r2, Region.Op.INTERSECT))
            && (!r1.quickReject(r3) && r1.op(r3, Region.Op.INTERSECT))
            && (!r1.quickReject(r4) && r1.op(r4, Region.Op.INTERSECT))
            && (!r1.quickReject(r4) && r1.op(r4, Region.Op.INTERSECT))
            && (!r2.quickReject(r3) && r2.op(r3, Region.Op.INTERSECT))
            && (!r2.quickReject(r4) && r2.op(r4, Region.Op.INTERSECT))
            && (!r3.quickReject(r4) && r3.op(r4, Region.Op.INTERSECT))
        ){
            // Collision!
        } else {
            Toast.makeText(getContext(),
                    R.string.missing_intersection,
                    Toast.LENGTH_SHORT).show();
            erase();
        }
    }

    private void touchEnded (int lineID){
        paintLine.setStyle(Paint.Style.FILL);
        paintLine.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SCREEN));
        if(regionD3) {
            Path path = pathMap.get(lineID);
            canvasBitmap.drawPath(path, paintLine);
            RectF rectF = new RectF();
            path.computeBounds(rectF, true);
            region4 = new Region();
            r4 = new Region();
            region4.setPath(path, new Region((int) rectF.left,(int) rectF.top,(int) rectF.right,(int) rectF.bottom));
            r4.setPath(path, new Region((int) rectF.left,(int) rectF.top,(int) rectF.right,(int) rectF.bottom));
            regionD4 = true;
            checkIntersection();
            path.reset();
        } else if(regionD2) {
            Path path = pathMap.get(lineID);
            canvasBitmap.drawPath(path, paintLine);
            RectF rectF = new RectF();
            path.computeBounds(rectF, true);
            region3 = new Region();
            r3 = new Region();
            region3.setPath(path, new Region((int) rectF.left,(int) rectF.top,(int) rectF.right,(int) rectF.bottom));
            r3.setPath(path, new Region((int) rectF.left,(int) rectF.top,(int) rectF.right,(int) rectF.bottom));
            regionD3 = true;
            path.reset();
        } else if(regionD) {
            Path path = pathMap.get(lineID);
            canvasBitmap.drawPath(path, paintLine);
            RectF rectF = new RectF();
            path.computeBounds(rectF, true);
            region2 = new Region();
            r2 = new Region();
            region2.setPath(path, new Region((int) rectF.left,(int) rectF.top,(int) rectF.right,(int) rectF.bottom));
            r2.setPath(path, new Region((int) rectF.left,(int) rectF.top,(int) rectF.right,(int) rectF.bottom));
            regionD2 = true;
            path.reset();
        } else {
            Path path = pathMap.get(lineID);
            canvasBitmap.drawPath(path, paintLine);
            RectF rectF = new RectF();
            path.computeBounds(rectF, true);
            region = new Region();
            r1 = new Region();
            region.setPath(path, new Region((int) rectF.left,(int) rectF.top,(int) rectF.right,(int) rectF.bottom));
            r1.setPath(path, new Region((int) rectF.left,(int) rectF.top,(int) rectF.right,(int) rectF.bottom));
            regionD = true;
            path.reset();
        }
        paintLine.setStyle(Paint.Style.STROKE);
    }

    private void
    touchMoved (MotionEvent event){

        for (int i = 0; i < event.getPointerCount(); i++){
            int pointerID = event.getPointerId(i);
            int pointerIndex = event.findPointerIndex(pointerID);
            if (pathMap.containsKey(pointerID)){
                float newX = event.getX(pointerIndex);
                float newY = event.getY(pointerIndex);
                Path path = pathMap.get(pointerID);
                Point point = previousPointMap.get(pointerID);
                float deltaX = Math.abs(newX - point.x);
                float deltaY = Math.abs(newY - point.y);
                if (deltaX >= TOUCH_TOLERANCE
                        || deltaY >= TOUCH_TOLERANCE){
                    path.quadTo(point.x, point.y,
                        (newX + point.x) / 2,
                        (newY + point.y) / 2);
                point.x = (int) newX;
                    point.y = (int) newY;}
            }
        }
    }


    public void erase() {
        clear();
        onSizeChanged(getWidth(), getHeight(), getWidth(), getHeight());
        added.clear();
        love.clear();
        paid.clear();
        need.clear();
        good.clear();
        profession.clear();
        passion.clear();
        vocation.clear();
        mission.clear();
    }

    private void touchStarted (float x, float y, int lineID) {
        Path path;
        Point point;
        if(pathMap.containsKey(lineID)) {
            path = pathMap.get(lineID);
            path.reset();
            point = previousPointMap.get(lineID);
        } else {
            path = new Path();
            pathMap.put(lineID, path);
            point = new Point();
            previousPointMap.put(lineID, point);
        }
        path.moveTo(x, y);
        point.x = (int) x;
        point.y = (int) y;
    }

    public void drawIkigai() {
        erase();
        randomizeColor(paintLine);
        Path path = new Path();
        path.addCircle(getWidth() / 2, getHeight() / 100 * 38 , RADIUS, Path.Direction.CCW);
        pathMap.put(AUTO_VALUE, path);
        touchEnded(AUTO_VALUE);
        randomizeColor(paintLine);
        path.addCircle(getWidth() / 10 * 3, getHeight() / 2, RADIUS, Path.Direction.CCW);
        pathMap.put(AUTO_VALUE, path);
        touchEnded(AUTO_VALUE);
        randomizeColor(paintLine);
        path.addCircle(getWidth() / 10 * 7, getHeight() / 2, RADIUS, Path.Direction.CCW);
        pathMap.put(AUTO_VALUE, path);
        touchEnded(AUTO_VALUE);
        randomizeColor(paintLine);
        path.addCircle(getWidth() / 2, getHeight() / 100 * 62, RADIUS, Path.Direction.CCW);
        pathMap.put(AUTO_VALUE, path);
        touchEnded(AUTO_VALUE);
        invalidate();
    }

    private void randomizeColor(Paint paintLine) {
        Random random = new Random();
        //int a = random.nextInt();
        int r = random.nextInt(255);
        int g = random.nextInt(255);
        int b = random.nextInt(255);
        paintLine.setColor(Color.argb(ALPHA, r, g, b));
    }

    public boolean addWord (String word) {
        switch (typing){
            case "love":
                if(!love.contains(word)) {
                    love.add(word);
                    return true;
                }
                break;
            case "good":
                if(!good.contains(word)) {
                    good.add(word);
                    return true;
                }
                break;
            case "need":
                if(!need.contains(word)) {
                    need.add(word);
                    return true;
                }
                break;
            case "paid":
                if(!paid.contains(word)) {
                    paid.add(word);
                    return true;
                }
                break;
            case "passion":
                if(!passion.contains(word)) {
                    passion.add(word);
                    return true;
                }
                break;
            case "profession":
                if(!profession.contains(word)) {
                    profession.add(word);
                    return true;
                }
                break;
            case "mission":
                if(!mission.contains(word)) {
                    mission.add(word);
                    return true;
                }
                break;
            case "vocation":
                if(!vocation.contains(word)) {
                    vocation.add(word);
                    return true;
                }
                break;
            case "ikigai":
                if(!added.contains(word)) {
                    added.add(word);
                    return true;
                }
                break;
        }
        return false;
    }
}



