package custom.view;

import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ehsan jelodar on 7/13/16 AD.
 */

public class UIJustifiedLabel extends android.support.v7.widget.AppCompatTextView {


    private final static String SYSTEM_NEWLINE = "\n";
    private final static char SYSTEM_NEWLINE_CHAR = '\n';
    private final static char SPACE_CHAR = ' ';
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
        //==   paint.setTextAlign(text_align);
        //== paint.setFlags(Paint.ANTI_ALIAS_FLAG);


        float dirtyRegionWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        if(dirtyRegionWidth < 0)// opt when error occur
        {
            return text;
        }
        int maxLines = getMaxLines();

        String[] blocks = text.split(SYSTEM_NEWLINE);
        float spaceOffset = paint.measureText(" ");
        List<String> lines = new ArrayList<>();

        ///////
        int block_index = 0;
        int block_len = blocks.length;
        for(String block : blocks)// create lines
        {
            block_index++;
            if (block.isEmpty()) {
                continue;
            }

            String line_text = block;
            while (true)
            {
                String wrappedLine = createWrappedLine(line_text, paint, spaceOffset, dirtyRegionWidth);

                line_text = line_text.substring(wrappedLine.length());
     
                if(line_text.isEmpty())// end of block
                {
                    if(block_index != block_len)// prevent add last \n
                    {
                        wrappedLine += SYSTEM_NEWLINE;
                    }
                    lines.add(wrappedLine);
                    break;
                }
                lines.add(wrappedLine);
            }

        }

        StringBuilder smb = new StringBuilder();
        int line_len = lines.size();
        int line_index = 0;
        boolean is_restrict_line = false;
        for(String line_txt : lines)
        {
            line_index++;

            if(line_txt.isEmpty())
            {
                continue;
            }

            char last_char = line_txt.charAt(line_txt.length() - 1);
            if(line_index == line_len || (last_char == SYSTEM_NEWLINE_CHAR))// prevent add space at original text in last line or, when new line char exist on text.
            {
                smb.append(line_txt);
            }
            else // append blank space to text.
            {
                StringBuilder s_tmp = new StringBuilder(line_txt);
                int index = -1;
                while (paint.measureText(line_txt) < dirtyRegionWidth)
                {
                    index = line_txt.indexOf(SPACE_CHAR, index + 2);// 2 for after of this and after of append space
                    if(index >= line_txt.lastIndexOf(SPACE_CHAR))
                    {
                        index = -1;
                    }
                    else
                    {
                        if(index != -1)
                        {
                            s_tmp.insert(index, ' ');
                            line_txt = s_tmp.toString();
                        }
                    }
                }
                smb.append(line_txt);
            }


            if (line_index == maxLines)//end of text
            {
                is_restrict_line = true;
                break;
            }

        }

        if(is_restrict_line)
        {
            String txt = smb.toString();
            txt = txt.replace(txt.substring(txt.length() - 3), "...");
            return txt;
        }
        return smb.toString();

    }



    private static String createWrappedLine(String block, Paint paint, float spaceOffset, float maxWidth)
    {
        float cacheWidth = 0;
        StringBuilder line = new StringBuilder();

        String[] words = block.split("\\s+");
        if(words.length > 1)// prevent lock on loop when space not exist on text.
        {
            for (String word : words)//or "\\s"
            {
                cacheWidth += paint.measureText(word);

                if (cacheWidth > maxWidth) {
                    return line.toString();
                }

                cacheWidth += spaceOffset;
                line.append(word).append(SPACE_CHAR);
            }
        }
        return block;
    }
    

}


