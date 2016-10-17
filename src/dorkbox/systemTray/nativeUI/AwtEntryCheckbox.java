/*
 * Copyright 2014 dorkbox, llc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dorkbox.systemTray.nativeUI;

import java.awt.CheckboxMenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import dorkbox.systemTray.Checkbox;
import dorkbox.systemTray.SystemTray;

class AwtEntryCheckbox extends AwtEntry implements Checkbox {

    private final ActionListener swingCallback;

    private volatile ActionListener callback;

    // this is ALWAYS called on the EDT.
    AwtEntryCheckbox(final AwtMenu parent, final ActionListener callback) {
        super(parent, new java.awt.CheckboxMenuItem());
        this.callback = callback;

        if (callback != null) {
            _native.setEnabled(true);
            swingCallback = new ActionListener() {
                @Override
                public
                void actionPerformed(ActionEvent e) {
                    // we want it to run on the EDT
                    handle();
                }
            };

            _native.addActionListener(swingCallback);
        } else {
            _native.setEnabled(false);
            swingCallback = null;
        }
    }

    /**
     * @return true if this checkbox is selected, false if not
     */
    public
    boolean getState() {
        return ((CheckboxMenuItem) _native).getState();
    }

    @Override
    public
    void setCallback(final ActionListener callback) {
        this.callback = callback;
    }

    private
    void handle() {
        ActionListener cb = this.callback;
        if (cb != null) {
            try {
                cb.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, ""));
            } catch (Throwable throwable) {
                SystemTray.logger.error("Error calling menu entry {} click event.", getText(), throwable);
            }
        }
    }

    // always called in the EDT
    @Override
    void renderText(final String text) {
        _native.setLabel(text);
    }


    // not supported!
    @Override
    public
    boolean hasImage() {
        return false;
    }

    // not supported!
    @Override
    void setImage_(final File imageFile) {
    }

    @Override
    void removePrivate() {
        _native.removeActionListener(swingCallback);
    }
}
