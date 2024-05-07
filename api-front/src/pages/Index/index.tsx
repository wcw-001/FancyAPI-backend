import {PageContainer} from '@ant-design/pro-components';
import React, {useEffect, useState} from 'react';
import {List, message} from "antd";
import {DataType} from "csstype";
import {listInterfaceInfoByPageUsingGET} from "@/services/yuapi-backend/interfaceInfoController";

/**
 * 主页
 * @constructor
 */
const Index: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [list, setList] = useState<DataType[]>([]);
  const [total,setTotal] = useState<number>(0);
  const loadData = async (current=1,pageSize=10) => {
    setLoading(true);
    try{
      const res = await listInterfaceInfoByPageUsingGET({
        current,
        pageSize
      });
      setList(res?.data?.records ?? []);
      setTotal(res?.data?.total ?? 0);
    }catch(error:any){
      message.error('请求失败，'+error.message);
    }
    setLoading(false);
  }
  useEffect(() => {
    loadData();
  },[])
  return (
    <PageContainer title="在线接口开发平台">
      <List
        className="demo-loadmore-list"
        loading={loading}
        itemLayout="horizontal"
        dataSource={list}
        renderItem={(item) => {
          const apiLink = `/interface_info/${item.id}`;
          return(<List.Item
            actions={[<a key={item.id} href={apiLink}>查看</a>]}
          >
            <List.Item.Meta
              title={<a href={apiLink}>{item.name}</a>}
              description={item.description}
            />
          </List.Item>
          );
        }}
        pagination={
          {
            showTotal(total){
              return '总数：'+total
            },
            pageSize: 10,
            total,
            onChange(page,pageSize){
              loadData(page,pageSize);
            }
          }
        }
      />
    </PageContainer>
    )
};

export default Index;
