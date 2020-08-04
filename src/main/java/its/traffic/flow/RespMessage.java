package its.traffic.flow;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * RespMessage
 *
 * @author wangshen
 * @version 1.0
 * 2020/6/3 15:31
 **/
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RespMessage implements Serializable {
    /**
     * 200=请求成功
     * 500=请求出错
     * */
    private int code;
    private String msg;

    //条数
    private Object total;
    //第几页
    private Object pageNum;
    //总页数
    private Object totalPage;
    //总条数
    private Object totalSum;

    //数据
    private Object data;

    public static RespMessage getEmptyMsg() {
        return new RespMessage(0,"",null);
    }

    public static RespMessage getSuccessMsg(){
        return new RespMessage(200,"request success!",null);
    }

    public static RespMessage getSuccessMsg(Object data){
        return new RespMessage(200,"request success!",data);
    }


    public static RespMessage getSuccessMsg(String msg){
        return new RespMessage(200,msg,null);
    }

    public static RespMessage getSuccessMsg(String msg, Object data){
        return new RespMessage(200,msg,data);
    }

    public static RespMessage getSuccessMsg(Object total, Object pageNum, Object totalPage,Object totalSum,Object data){
        return new RespMessage(200,"request success!",total,pageNum,totalPage,totalSum,data);
    }



    public static RespMessage getFailureMsg(){
        return getFailureMsg("request error!");
    }

    public static RespMessage getFailureMsg(String msg){
        return new RespMessage(500,msg,null);
    }


    public static RespMessage getRespMessage(int code, String msg, Object data){
        return new RespMessage(code,msg,data);
    }
    public RespMessage(){

    }
    private RespMessage(int code, String msg, Object data){
        this(code,msg,null,null,null,null,data);
    }

    private RespMessage(int code, String msg, Object total, Object pageNum, Object totalPage,Object totalSum, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
        this.total=total;
        this.pageNum=pageNum;
        this.totalPage=totalPage;
        this.totalSum=totalSum;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Object getTotal() {
        return total;
    }

    public void setTotal(Object total) {
        this.total = total;
    }

    public Object getPageNum() {
        return pageNum;
    }

    public void setPageNum(Object pageNum) {
        this.pageNum = pageNum;
    }

    public Object getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(Object totalPage) {
        this.totalPage = totalPage;
    }

    public Object getTotalSum() {
        return totalSum;
    }

    public void setTotalSum(Object totalSum) {
        this.totalSum = totalSum;
    }

    @Override
    public String toString() {
        return "jamReportMessage{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", total=" + total +
                ", pageNum=" + pageNum +
                ", totalPage=" + totalPage +
                ", totalSum=" + totalSum +
                ", data=" + data +
                '}';
    }
}
