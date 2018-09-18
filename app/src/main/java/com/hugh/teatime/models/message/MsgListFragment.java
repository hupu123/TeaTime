package com.hugh.teatime.models.message;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.hugh.teatime.R;
import com.hugh.teatime.utils.SMSUtil;
import com.hugh.teatime.utils.SPUtil;

import java.util.List;

/**
 * Created by Hugh on 2016/4/15 13:50
 */
public class MsgListFragment extends Fragment {

    private Context context;
    private List<SMS> smses;
    private SMSListAdapter sla;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.context = getContext();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View viewContent = inflater.inflate(R.layout.fragment_sms_list, null, false);

        initView(viewContent);

        return viewContent;
    }

    @Override
    public void onResume() {
        super.onResume();

        smses.clear();
        smses.addAll(SMSUtil.getSmsFromPhone(context, SPUtil.getInstance(context).getSMSBlackList(), 0));
        sla.notifyDataSetChanged();
    }

    /**
     * 初始化控件
     *
     * @param view Root控件
     */
    private void initView(View view) {

        ListView lvAllSMS = (ListView) view.findViewById(R.id.lv_all_sms_list);

        smses = SMSUtil.getSmsFromPhone(context, SPUtil.getInstance(context).getSMSBlackList(), 0);
        sla = new SMSListAdapter(context, smses);
        lvAllSMS.setAdapter(sla);
        lvAllSMS.setOnItemClickListener(itemClickListener);
    }

    /**
     * ListView item点击事件监听
     */
    AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            Intent intent = new Intent(context, SMSDetailActivity.class);
            SMS sms = smses.get(position);
            sms.setClassType(0);
            intent.putExtra(MsgHomeActivity.INTENT_SMS_INFO, sms);
            startActivity(intent);
        }
    };
}
