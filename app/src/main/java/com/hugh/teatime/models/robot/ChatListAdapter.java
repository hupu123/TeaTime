package com.hugh.teatime.models.robot;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.hugh.teatime.R;
import com.hugh.teatime.app.GlobalVar;
import com.hugh.teatime.utils.ToolUtil;

import java.util.List;

/**
 * Created by Hugh on 2016/2/15 14:50
 */
public class ChatListAdapter extends BaseAdapter {

    private List<Message> messages;
    private Context context;

    public ChatListAdapter(Context context, List<Message> messages) {

        this.context = context;
        this.messages = messages;
    }

    @Override
    public int getCount() {
        if (messages != null) {
            return messages.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        final Message message = messages.get(position);

        //        if (convertView == null) {
        LayoutInflater inflater = LayoutInflater.from(context);
        viewHolder = new ViewHolder();

        if (message.getType() == 0) {// 机器人消息
            convertView = inflater.inflate(R.layout.item_chat_left_lv, null);

            viewHolder.tvMsg = (TextView) convertView.findViewById(R.id.tv_lv_chat_left);
            viewHolder.tvMsg.setText(message.getMsg());

            switch (message.getCode()) {
                case GlobalVar.MSG_TYPE_TEXT:// 文字类消息
                    break;
                case GlobalVar.MSG_TYPE_URL:// 链接类消息
                    viewHolder.tvUrl = (TextView) convertView.findViewById(R.id.tv_lv_chat_left_2);
                    viewHolder.tvUrl.setVisibility(View.VISIBLE);
                    viewHolder.tvUrl.setText(message.getUrl());
                    break;
                case GlobalVar.MSG_TYPE_NEWS:// 新闻类消息
                    viewHolder.lvListInfo = (ListView) convertView.findViewById(R.id.lv_lv_chat_left);
                    viewHolder.lvListInfo.setVisibility(View.VISIBLE);
                    NewsListAdapter nla = new NewsListAdapter(context, message.getNewsList());
                    viewHolder.lvListInfo.setAdapter(nla);
                    ToolUtil.setListViewHeightBasedOnChildren(viewHolder.lvListInfo);
                    viewHolder.lvListInfo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(message.getNewsList().get(position).getDetailurl())));
                        }
                    });
                    break;
                case GlobalVar.MSG_TYPE_COOK_BOOK:// 菜谱类消息
                    viewHolder.lvListInfo = (ListView) convertView.findViewById(R.id.lv_lv_chat_left);
                    viewHolder.lvListInfo.setVisibility(View.VISIBLE);
                    CookBookListAdapter cbla = new CookBookListAdapter(context, message.getCookBookList());
                    viewHolder.lvListInfo.setAdapter(cbla);
                    ToolUtil.setListViewHeightBasedOnChildren(viewHolder.lvListInfo);
                    viewHolder.lvListInfo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(message.getCookBookList().get(position).getDetailurl())));
                        }
                    });
                    break;
                case GlobalVar.MSG_TYPE_SONG:// 儿歌类消息
                    break;
                case GlobalVar.MSG_TYPE_POETRY:// 诗词类消息
                    break;
                default:
                    break;
            }
        } else {// 用户消息
            convertView = inflater.inflate(R.layout.item_chat_right_lv, null);
            viewHolder.tvMsg = (TextView) convertView.findViewById(R.id.tv_lv_chat_right);

            viewHolder.tvMsg.setText(message.getMsg());
        }
        convertView.setTag(viewHolder);
        //        } else {
        //            viewHolder = (ViewHolder) convertView.getTag();
        //        }

        return convertView;
    }

    private class ViewHolder {

        TextView tvMsg;
        TextView tvUrl;
        ImageView ivImage;
        ListView lvListInfo;
    }
}
