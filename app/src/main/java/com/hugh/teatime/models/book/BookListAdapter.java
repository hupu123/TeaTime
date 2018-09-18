package com.hugh.teatime.models.book;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hugh.teatime.R;
import com.hugh.teatime.utils.ToolUtil;

import java.util.List;

/**
 * Created by Hugh on 2016/3/17 14:55
 */
public class BookListAdapter extends BaseAdapter {

    private Context context;
    private List<Book> books;

    public BookListAdapter(Context context, List<Book> books) {

        this.context = context;
        this.books = books;
    }

    @Override
    public int getCount() {

        if (books != null) {
            return books.size();
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
        Book book = books.get(position);
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_books_lv, null);
            viewHolder.tvBookName = (TextView) convertView.findViewById(R.id.tv_book_name);
            viewHolder.tvReadProgress = (TextView) convertView.findViewById(R.id.tv_read_progress);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tvBookName.setText(book.getName());
        viewHolder.tvReadProgress.setText(ToolUtil.getProgress(book.getProgress() - 1, book.getSize()) + "%");

        return convertView;
    }

    private class ViewHolder {

        // 书名
        TextView tvBookName;
        // 阅读进度
        TextView tvReadProgress;
    }
}
