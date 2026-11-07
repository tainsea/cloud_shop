package cn.fourse.orderservice.mapper;

import cn.fourse.orderservice.entity.MqConsumeRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MqConsumeRecordMapper extends BaseMapper<MqConsumeRecord> {
}
