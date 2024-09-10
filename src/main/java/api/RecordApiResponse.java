package api;

import java.util.List;

import api.ApiResponse;
import api.RecordPojo;

public class RecordApiResponse extends ApiResponse {

    private DataWrapper data;  // data 是一个对象

    public DataWrapper getData() {
        return data;
    }

    public void setData(DataWrapper data) {
        this.data = data;
    }

    public static class DataWrapper {
        private List<RecordPojo> records;

        public List<RecordPojo> getRecords() {
            return records;
        }

        public void setRecords(List<RecordPojo> records) {
            this.records = records;
        }
    }
}
