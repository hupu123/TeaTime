package com.hugh.teatime.models.home;

import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.codbking.widget.DatePickDialog;
import com.codbking.widget.OnSureLisener;
import com.codbking.widget.bean.DateType;
import com.hugh.teatime.R;
import com.hugh.teatime.models.note.NoteAlarmUitls;
import com.hugh.teatime.utils.StringUtil;

import java.util.Date;

public class SettingsFragment extends PreferenceFragmentCompat {

    public static final String PREFERENCE_NAME = "sp_teatime_settings";
    public static final String PREFERENCE_NOTE_SWITCH = "preference_note_switch";
    public static final String PREFERENCE_NOTE_TIME = "preference_note_time";

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        getPreferenceManager().setSharedPreferencesName(PREFERENCE_NAME);
        addPreferencesFromResource(R.xml.preference_settings);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        updateSwitchTime();
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        switch (preference.getKey()) {
            case PREFERENCE_NOTE_SWITCH:
                updateSwitchTime();
                break;
            case PREFERENCE_NOTE_TIME:
                DatePickDialog dpd = new DatePickDialog(getContext());
                dpd.setType(DateType.TYPE_HM);
                dpd.setStartDate(new Date());
                dpd.setTitle(getResources().getString(R.string.bill_record_select_date));
                dpd.setOnSureLisener(new OnSureLisener() {
                    @Override
                    public void onSure(Date date) {
                        String time = StringUtil.formatTimestamp4(date.getTime());
                        getPreferenceScreen().findPreference(PREFERENCE_NOTE_TIME).setSummary(time);
                        getPreferenceManager().getSharedPreferences().edit().putString(PREFERENCE_NOTE_TIME, time).apply();
                        NoteAlarmUitls.openAlarmNotice(getContext(), time);
                    }
                });
                dpd.show();
                break;
            default:
                break;
        }
        return super.onPreferenceTreeClick(preference);
    }

    /**
     * 更新闹钟事件选择控件
     */
    private void updateSwitchTime() {
        boolean isOpen = getPreferenceManager().getSharedPreferences().getBoolean(PREFERENCE_NOTE_SWITCH, false);
        String time = getPreferenceManager().getSharedPreferences().getString(PREFERENCE_NOTE_TIME, "00:00");
        getPreferenceScreen().findPreference(PREFERENCE_NOTE_TIME).setEnabled(isOpen);
        getPreferenceScreen().findPreference(PREFERENCE_NOTE_TIME).setSummary(time);
        if (isOpen) {
            NoteAlarmUitls.openAlarmNotice(getContext(), time);
        } else {
            NoteAlarmUitls.cancelAlarmNotice(getContext());
        }
    }
}
