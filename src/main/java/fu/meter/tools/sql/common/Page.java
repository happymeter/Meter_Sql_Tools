package fu.meter.tools.sql.common;

import lombok.Data;

import java.util.Collections;
import java.util.List;

/**
 * @author meter
 * @version V1.0
 * Copyright ©2021
 * @ClassName Page
 * @desc 用于分页查询数据库
 * @date 2021/12/23 17:46
 */
@Data
public class Page<T> {

    /**
     * 查询数据列表
     */
    protected List<T> records ;

    /**
     * 总数
     */
    protected Integer total = null;
    /**
     * 每页显示条数，默认 10
     */
    protected Integer size = 10;
    /**
     * 限制总数量
     */
    protected Integer limit ;

    /**
     * 当前页
     */
    protected Integer current = 1;



    public Integer getPages() {
        if (getSize() == 0) {
            return 0;
        }
        Integer pages = getTotal() / getSize();
        if (getTotal() % getSize() != 0) {
            pages++;
        }
        return pages;
    }
    public Integer offset() {
        long current = getCurrent();
        if (current <= 1L) {
            return 0;
        }
        final int index = Math.toIntExact(Math.max((current - 1) * getSize(), 0));
        if(limit != null && limit>0){
            //开启了总数限制
            if(current==0 && size>limit){
                setSize(limit);
            }else {
                if((index+size)>limit){
                    setSize(limit-index);
                }
            }
        }

        return index;
    }
}
