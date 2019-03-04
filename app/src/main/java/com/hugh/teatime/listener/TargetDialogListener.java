package com.hugh.teatime.listener;

import com.hugh.teatime.models.target.TargetBean;

public interface TargetDialogListener {

    void sure(TargetBean targetBean);

    void delete();

    void cancel();
}
