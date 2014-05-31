/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2012-2014 lacolaco.net
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.lacolaco.smileessence.view.dialog;

import android.app.DialogFragment;
import android.view.View;
import android.widget.AdapterView;
import net.lacolaco.smileessence.R;
import net.lacolaco.smileessence.command.Command;
import net.lacolaco.smileessence.command.IConfirmable;
import net.lacolaco.smileessence.data.CommandSettingCache;

import java.util.Iterator;
import java.util.List;

public abstract class MenuDialogFragment extends DialogFragment
{

    // ------------------------------ FIELDS ------------------------------

    protected final AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
        {
            final Command command = (Command) adapterView.getItemAtPosition(i);
            if(command != null)
            {
                if(command instanceof IConfirmable)
                {
                    ConfirmDialogFragment.show(getActivity(), getString(R.string.dialog_confirm_commands), new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            DialogHelper.close(getActivity());
                            command.execute();
                        }
                    });
                }
                else
                {
                    DialogHelper.close(getActivity());
                    command.execute();
                }
            }
        }
    };

    protected final void filterCommands(List<Command> commands)
    {
        Iterator<Command> iterator = commands.iterator();
        while(iterator.hasNext())
        {
            Command command = iterator.next();
            if(!command.isEnabled())
            {
                iterator.remove();
            }
            else if(command.getKey() >= 0)
            {
                boolean visibility = CommandSettingCache.getInstance().get(command.getKey());
                if(!visibility)
                {
                    iterator.remove();
                }
            }
        }
    }
}
