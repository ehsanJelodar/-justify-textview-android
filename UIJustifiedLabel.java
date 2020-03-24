package custom.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;


public class UIJustifiedLabel extends android.support.v7.widget.AppCompatTextView {

    private final static String SYSTEM_NEWLINE = "\n";
    private boolean isJustChangeString  = false;
    private String tmpString;

    public UIJustifiedLabel(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public UIJustifiedLabel(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public UIJustifiedLabel(Context context) {
        super(context);
    }


    @Override
    public void setText(CharSequence text, BufferType type) {
        if(!isJustChangeString)
        {
            tmpString = text.toString();
        }
        super.setText(text, type);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void changeText(String text)
    {
        isJustChangeString = true;
        setText(text);
        isJustChangeString = false;
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        if(!isInEditMode())
        {
            String str = justifyText(tmpString);
            changeText(str);
            //setTextDirection(TEXT_DIRECTION_RTL);
        }

    }

    private String justifyText(String text)
    {
        Paint paint =  new Paint();
        //==   paint.setColor(getCurrentTextColor());
        paint.setTypeface(getTypeface());
        paint.setTextSize(getTextSize());

        // minus out the padding pixel
        float dirtyRegionWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        if(dirtyRegionWidth < 0)// opt when error occur
        {
            return text;
        }
        int maxLines = getMaxLines();

        int lines = 1;
        String[] blocks = text.split("((?<=\n)|(?=\n))");
        // fix
        float spaceOffset = paint.measureText(" ");

        StringBuilder smb = new StringBuilder();
        for (int i = 0; i < blocks.length && (lines <= maxLines); i++)
        {

            String block = blocks[i];

            if (block.isEmpty()) {
                continue;
            } else if (block.equals(SYSTEM_NEWLINE)) {
                smb.append(block);
                continue;
            }


            Object[] wrappedObj = createWrappedLine(block, paint, spaceOffset, dirtyRegionWidth);

            String wrappedLine = ((String) wrappedObj[0]);
            float wrappedEdgeSpace = (Float) wrappedObj[1];
            String[] lineAsWords = wrappedLine.split(" ");
            int spacesToSpread = (int) (wrappedEdgeSpace != Float.MIN_VALUE ? wrappedEdgeSpace / spaceOffset : 0);

            for (int j = 0; j < lineAsWords.length; j++)
            {
                String word = lineAsWords[j];
                if (lines == maxLines && j == (lineAsWords.length - 1))
                {
                    smb.append("...");//end of text
                    continue;// or break
                }
                else
                {
                    smb.append(word).append(" ");
                }

                if (--spacesToSpread > 0) {
                    smb.append(" ");
                }
            }

            lines++;

            smb = new StringBuilder(smb.toString()); // may opt // smb.toString().trim()


            if (!blocks[i].isEmpty())
            {
                if(lineAsWords.length > 2)// prevent lock on loop
                {
                    blocks[i] = blocks[i].substring(wrappedLine.length());

                    if (!blocks[i].isEmpty())
                    {
                        smb.append(SYSTEM_NEWLINE);
                    }
                    i--;
                }
                else
                {
                    smb.append(blocks[i]);
                }
            }
        }
        return smb.toString();
    }



    private static Object[] createWrappedLine(String block, Paint paint, float spaceOffset, float maxWidth) {
        float cacheWidth;
        float origMaxWidth = maxWidth;

        StringBuilder line = new StringBuilder();

        for (String word : block.split("\\s")) {
            cacheWidth = paint.measureText(word);
            maxWidth -= cacheWidth;

            if (maxWidth <= 0) {
                return new Object[] {line.toString(), maxWidth + cacheWidth + spaceOffset };
            }

            line.append(word).append(" ");
            maxWidth -= spaceOffset;
        }

        if (paint.measureText(block) <= origMaxWidth) {
            return new Object[] { block, Float.MIN_VALUE };
        }

        return new Object[] {line.toString(), maxWidth };
    }

}
