package usi.justmove.UI;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import usi.justmove.R;

/**
 * Created by usi on 19/02/17.
 */

public class ExpandableLayout extends LinearLayout {
    private RelativeLayout title;
    private LinearLayout body;

    private TextView noContentMsg;
    private LinearLayout bodyContent;

    private LinearLayout titleContent;
    private ImageView titleArrow;

    private boolean isExpanded;
    private boolean showNoContentMsg;
    private int angle;
    private Context context;
    private AttributeSet attrs;
    public ExpandableLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.context = context;
        this.attrs = attrs;
        init();

    }

    private void init() {
        isExpanded = false;
        showNoContentMsg = false;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.expandable_layout, this, true);

        title = (RelativeLayout) this.findViewById(R.id.expandableLayoutTitle);
        titleContent = (LinearLayout) this.findViewById(R.id.expandableLayoutTitleContent);
        titleArrow = (ImageView) this.findViewById(R.id.expandableLayoutTitleArrow);
        body = (LinearLayout) this.findViewById(R.id.expandableLayoutBody);
        bodyContent = (LinearLayout) this.findViewById(R.id.expandableLayoutBodyContent);
        noContentMsg = (TextView) this.findViewById(R.id.expandableLayoutNoContentMsg);

        if(showNoContentMsg) {
            noContentMsg.setVisibility(VISIBLE);
            bodyContent.setVisibility(GONE);
        } else {
            noContentMsg.setVisibility(GONE);
            bodyContent.setVisibility(VISIBLE);
        }

        title.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isExpanded) {
                    collapse();
                } else {
                    expand();
                }
            }
        });

        TypedArray customStyle = context.obtainStyledAttributes(attrs, R.styleable.expandableLayout);
        if (customStyle == null) {
            return;
        }

        isExpanded = customStyle.getBoolean(R.styleable.expandableLayout_initExpanded, false);

        if(isExpanded) {
            body.setVisibility(VISIBLE);
            titleArrow.setImageResource(R.drawable.collapse_arrow);
        } else {
            body.setVisibility(GONE);
            titleArrow.setImageResource(R.drawable.expand_arrow);
        }
    }

    public void setTitleView(View view) {
        titleContent.addView(view);
    }

    public void setBodyView(View view) {
        bodyContent.addView(view);
    }

    public void setNoContentMsg(String msg) {
        noContentMsg.setText(msg);
    }

    public void showBody() {
        bodyContent.setVisibility(VISIBLE);
        noContentMsg.setVisibility(GONE);
    }

    public void showNoContentMsg() {
        noContentMsg.setVisibility(VISIBLE);
        bodyContent.setVisibility(GONE);
    }

    public void setTitleText(int textId, String text) {
        ((TextView) titleContent.findViewById(textId)).setText(text);
    }

    public void setTitleImage(int imageId, int imageResourceId) {
        ((ImageView) titleContent.findViewById(imageId)).setImageResource(imageResourceId);
    }

    public void expand() {
//        body.setVisibility(VISIBLE);
//        noContentMsg.setVisibility(VISIBLE);
        titleArrow.setImageResource(R.drawable.expand_arrow);
        isExpanded = true;
        animateExpansion(body);
        ObjectAnimator anim = ObjectAnimator.ofFloat(titleArrow, "rotation", -angle, -1*(angle + 90));
        anim.setDuration(500);
        anim.start();
        angle += 90;
        angle = angle%90;
    }

    public void collapse() {
//        body.setVisibility(GONE);
        titleArrow.setImageResource(R.drawable.collapse_arrow);
        animateCollapse(body);
        isExpanded = false;
        ObjectAnimator anim = ObjectAnimator.ofFloat(titleArrow, "rotation", angle, angle + 90);
        anim.setDuration(500);
        anim.start();
        angle += 90;
        angle = angle%90;
    }

    private void animateCollapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1){
                    v.setVisibility(View.GONE);
                }else{
                    v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int)(initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    private void animateExpansion(final View v) {
        v.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? LinearLayout.LayoutParams.WRAP_CONTENT
                        : (int)(targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int)(targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public View getTitleView() {
        return titleContent;
    }

    public View getBodyView() {
        return bodyContent;
    }

    public void removeBody() {
        bodyContent.removeAllViews();
    }
}
