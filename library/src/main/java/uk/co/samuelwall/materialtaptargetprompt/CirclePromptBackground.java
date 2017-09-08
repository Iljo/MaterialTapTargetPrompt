/*
 * Copyright (C) 2017 Samuel Wall
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.co.samuelwall.materialtaptargetprompt;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;

public class CirclePromptBackground extends PromptBackground
{
    PointF mPosition;
    float mRadius;
    PointF mBasePosition;
    float mBaseRadius;
    Paint mPaint;
    int mBaseColourAlpha;

    public CirclePromptBackground()
    {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPosition = new PointF();
        mBasePosition = new PointF();
    }


    @Override
    public void setColour(int colour)
    {
        mPaint.setColor(colour);
        mBaseColourAlpha = Color.alpha(colour);
        mPaint.setAlpha(mBaseColourAlpha);
    }

    @Override
    public void prepare(final MaterialTapTargetPrompt prompt, final float maxTextWidth)
    {
        /*textWidth = (int) maxTextWidth;
        padding = (int) mTextPadding;*/
        final PromptFocal promptFocal = prompt.getPromptFocal();
        final RectF focalBounds = promptFocal.getBounds();
        final float focalCentreX = focalBounds.centerX();
        final float focalCentreY = focalBounds.centerY();
        final float focalPadding = promptFocal.getPadding();
        final RectF textBounds = prompt.getTextBounds();
        if (prompt.mInside88dpBounds)
        {
            float x1 = focalCentreX;
            float x2 = prompt.mView.mPrimaryTextLeft - prompt.mTextPadding;
            float y1, y2;
            if (prompt.mVerticalTextPositionAbove)
            {
                y1 = focalBounds.bottom + prompt.mTextPadding;
                y2 = textBounds.top;
            }
            else
            {
                y1 = focalBounds.top - (focalPadding + prompt.mTextPadding);
                y2 = textBounds.bottom;
            }

            final float y3 = y2;
            float x3 = x2 + maxTextWidth + prompt.mTextPadding + prompt.mTextPadding;

            final float focalLeft = focalBounds.left - focalPadding;
            final float focalRight = focalBounds.right + focalPadding;
            if (x2 > focalLeft && x2 < focalRight)
            {
                if ( prompt.mVerticalTextPositionAbove)
                {
                    x1 = focalBounds.left - focalPadding;
                }
                else
                {
                    x2 -= (focalBounds.width() / 2) - focalPadding;
                }
            }
            else if (x3 > focalLeft && x3 < focalRight)
            {
                if ( prompt.mVerticalTextPositionAbove)
                {
                    x1 = focalBounds.right + focalPadding;
                }
                else
                {
                    x3 += (focalBounds.width() / 2) + focalPadding;
                }
            }

            final double offset = Math.pow(x2, 2) + Math.pow(y2, 2);
            final double bc = (Math.pow(x1, 2) + Math.pow(y1, 2) - offset) / 2.0;
            final double cd = (offset - Math.pow(x3, 2) - Math.pow(y3, 2)) / 2.0;
            final double det = (x1 - x2) * (y2 - y3) - (x2 - x3) * (y1 - y2);
            final double idet = 1 / det;
            mBasePosition.set((float) ((bc * (y2 - y3) - cd * (y1 - y2)) * idet),
                    (float) ((cd * (x1 - x2) - bc * (x2 - x3)) * idet));
            mBaseRadius = (float) Math.sqrt(Math.pow(x2 - mBasePosition.x, 2)
                    + Math.pow(y2 - mBasePosition.y, 2));
            /*point1.set(x1, y1);
            point2.set(x2, y2);
            point3.set(x3, y3);*/
        }
        else
        {
            mBasePosition.set(focalCentreX, focalCentreY);
            final float length = Math.abs(
                    (prompt.mHorizontalTextPositionLeft ?
                            textBounds.left - prompt.mTextPadding
                            : textBounds.right + prompt.mTextPadding)
                    - focalCentreX);
            float height = (focalBounds.height() / 2) + focalPadding + textBounds.height();
            mBaseRadius = (float) Math.sqrt(Math.pow(length, 2) + Math.pow(height, 2));
            /*point1.set(focalCentreX + (prompt.mHorizontalTextPositionLeft ? -length : length),
                            focalCentreY + (prompt.mVerticalTextPositionAbove ? - height : height));*/
        }
        mPosition.set(mBasePosition);
    }

    @Override
    public void update(final MaterialTapTargetPrompt prompt, float revealAmount, float alphaModifier)
    {
        final RectF focalBounds = prompt.getPromptFocal().getBounds();
        final float focalCentreX = focalBounds.centerX();
        final float focalCentreY = focalBounds.centerY();
        mRadius = mBaseRadius * revealAmount;
        mPaint.setAlpha((int) (mBaseColourAlpha * alphaModifier));
        mPosition.set(focalCentreX + ((mBasePosition.x - focalCentreX) * revealAmount),
                focalCentreY + ((mBasePosition.y - focalCentreY) * revealAmount));
    }

    @Override
    public void draw(Canvas canvas)
    {
        canvas.drawCircle(mPosition.x, mPosition.y, mRadius, mPaint);
    }

    @Override
    public boolean contains(float x, float y)
    {
        return PromptUtils.isPointInCircle(x, y, mPosition, mRadius);
    }
}
