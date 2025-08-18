/*******************************************************************************
 * Copyright 2017 Bstek
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package com.bstek.ureport.chart.option.impl;

import com.bstek.ureport.chart.FontStyle;
import com.bstek.ureport.chart.option.Option;
import com.bstek.ureport.chart.option.Position;

/**
 * @author Jacky.gao
 * @since 2017年6月8日
 */
public class TitleOption implements Option {
    private boolean display;
    private Position position = Position.top;
    private int fontSize = 14;
    private String fontColor = "#666";
    private FontStyle fontStyle = FontStyle.bold;
    private int padding = 10;
    private String text;

    @Override
    public String buildOptionJson() {
        return "\"title\":{" +
                "\"display\":" + display + "," +
                "\"text\":\"" + text + "\"," +
                "\"position\":\"" + position + "\"," +
                "\"fontSize\":" + fontSize + "," +
                "\"fontColor\":\"" + fontColor + "\"," +
                "\"fontStyle\":\"" + fontStyle + "\"," +
                "\"padding\":\"" + padding + "\"" +
                "}";
    }

    @Override
    public String getType() {
        return "title";
    }

    public boolean isDisplay() {
        return display;
    }

    public void setDisplay(boolean display) {
        this.display = display;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    public String getFontColor() {
        return fontColor;
    }

    public void setFontColor(String fontColor) {
        this.fontColor = fontColor;
    }

    public FontStyle getFontStyle() {
        return fontStyle;
    }

    public void setFontStyle(FontStyle fontStyle) {
        this.fontStyle = fontStyle;
    }

    public int getPadding() {
        return padding;
    }

    public void setPadding(int padding) {
        this.padding = padding;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
