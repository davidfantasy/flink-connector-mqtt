package com.github.davidfantasy.flink.connector.mqtt.source;

import com.github.davidfantasy.flink.connector.mqtt.MqttMessage;
import org.apache.flink.connector.base.source.reader.RecordsWithSplitIds;
import org.apache.flink.table.data.RowData;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

/**
 * 对MqttSourceSplitReader每次fetch的结果进行封装
 */
public class MqttRecords implements RecordsWithSplitIds<RowData> {

    private String currentSplitId;

    private final Iterator<RowData> fetchedDatas;

    public MqttRecords(String currentSplitId, Iterator<RowData> fetchedDatas) {
        this.currentSplitId = currentSplitId;
        this.fetchedDatas = fetchedDatas;
    }

    @Nullable
    @Override
    public String nextSplit() {
        //如果一个RecordsWithSplitIds中获取多个分片的数据，那么需要按分片分别返回其对应的数据
        //nextSplit仅当在一个分片处理完成后才应该切换，如果RecordsWithSplitIds中所有分片的数据都
        //处理完成了，切记nextSplit返回null，否则会被认为当前RecordsWithSplitIds没有处理完成而
        //一直阻塞，从而不会触发下一次的fetch()!!!!!!!!!!!!!!!!!!
        return currentSplitId;
    }

    @Nullable
    @Override
    public RowData nextRecordFromSplit() {
        if (fetchedDatas == null) {
            return null;
        }
        if (fetchedDatas.hasNext()) {
            return fetchedDatas.next();
        }
        //注意:在当前RecordsWithSplitIds已经没有数据的情况下，nextSplit一定要返回空，否则会一直阻塞。
        //因为SourceReaderBase中，是通过判断RecordsWithSplitIds.nextSplit返回null
        //来判断当前Fetch是否已经处理完成了
        currentSplitId = null;
        return null;
    }

    @Override
    public Set<String> finishedSplits() {
        return Collections.emptySet();
    }

}
