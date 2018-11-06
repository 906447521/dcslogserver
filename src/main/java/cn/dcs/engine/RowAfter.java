package cn.dcs.engine;

import cn.dcs.engine.result.ResultAuth;

@Deprecated
public interface RowAfter {

    public void doAfter(int rowNum, EtlLog log, ResultAuth result);

}
