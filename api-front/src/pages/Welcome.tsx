import { PageContainer } from '@ant-design/pro-components';
import { useModel } from '@umijs/max';
import { Card, theme } from 'antd';
import React from 'react';
import {LEAPI_CLIENT_SDK} from "@/constants";

/**
 * 每个单独的卡片，为了复用样式抽成了组件
 * @param param0
 * @returns
 */
const InfoCard: React.FC<{
  title: string;
  index: number;
  desc: string;
  href: string;
}> = ({ title, href, index, desc }) => {
  const { useToken } = theme;
  const { token } = useToken();
  return (
    <div
      style={{
        backgroundColor: token.colorBgContainer,
        boxShadow: token.boxShadow,
        borderRadius: '8px',
        fontSize: '14px',
        color: token.colorTextSecondary,
        lineHeight: '22px',
        padding: '16px 19px',
        minWidth: '220px',
        flex: 1,
      }}
    >
      <div
        style={{
          display: 'flex',
          gap: '4px',
          alignItems: 'center',
        }}
      >
        <div
          style={{
            width: 48,
            height: 48,
            lineHeight: '22px',
            backgroundSize: '100%',
            textAlign: 'center',
            padding: '8px 16px 16px 12px',
            color: '#FFF',
            fontWeight: 'bold',
            backgroundImage:
              "url('https://gw.alipayobjects.com/zos/bmw-prod/daaf8d50-8e6d-4251-905d-676a24ddfa12.svg')",
          }}
        >
          {index}
        </div>
        <div
          style={{
            fontSize: '16px',
            color: token.colorText,
            paddingBottom: 8,
          }}
        >
          {title}
        </div>
      </div>
      <div
        style={{
          fontSize: '14px',
          color: token.colorTextSecondary,
          textAlign: 'justify',
          lineHeight: '22px',
          marginBottom: 8,
        }}
      >
        {desc}
      </div>

    </div>
  );
};

const Welcome: React.FC = () => {
  const { token } = theme.useToken();
  const { initialState } = useModel('@@initialState');
  return (
    <PageContainer>
      <Card
        style={{
          borderRadius: 8,
        }}
        bodyStyle={{
          backgroundImage:
            initialState?.settings?.navTheme === 'realDark'
              ? 'background-image: linear-gradient(75deg, #1A1B1F 0%, #191C1F 100%)'
              : 'background-image: linear-gradient(75deg, #FBFDFF 0%, #F5F7FF 100%)',
        }}
      >
        <div
          style={{
            backgroundPosition: '100% -30%',
            backgroundRepeat: 'no-repeat',
            backgroundSize: '274px auto',
            backgroundImage:
              "url('https://gw.alipayobjects.com/mdn/rms_a9745b/afts/img/A*BuFmQqsB2iAAAAAAAAAAAAAAARQnAQ')",
          }}
        >
          <div
            style={{
              fontSize: '20px',
              color: token.colorTextHeading,
            }}
          >
            欢迎使用 FancyAPI 接口开放平台 🎉
          </div>
          <p
            style={{
              fontSize: '14px',
              color: token.colorTextSecondary,
              lineHeight: '22px',
              marginTop: 16,
              marginBottom: 32,
              width: '65%',
            }}
          >
            FancyAPI 接口开放平台是一个为用户和开发者提供丰富 API 接口调用服务的平台 🛠
          </p>
          <p>
            💻 作为『开发者』，可以在线选择所需接口并通过导入 <a href={ LEAPI_CLIENT_SDK} target='_blank'> FancyApi-client-sdk </a> 快速在项目中集成调用接口的客户端，通过配置客户端的用户凭证快速调用接口，减轻开发成本，简化开发。
          </p>
          <p>
            😀 作为『用户』，可以查看接口列表，选择感兴趣的接口查看接口文档，在线调用接口，快速查看接口的返回值，判断接口的实现功能。
          </p>
          <p>
            🤝 作为『管理员』，可以管理接口和用户，管理接口时可以修改接口信息、上线、添加、发布和下线接口。管理用户时可以修改用户信息、禁用用户和解除用户的禁用等。
          </p>
          <div
            style={{
              display: 'flex',
              flexWrap: 'wrap',
              gap: 16,
            }}
          >
            <InfoCard
              index={1}
              title="多样化的接口选择"
              desc="FancyAPI 提供了丰富多样的接口用您选择，涵盖了开发中常用的功能，满足您的不同需求。"
            />
            <InfoCard
              index={2}
              title="在线调试功能"
              desc="您可以在平台上进行接口在线调试，快速验证接口的功能和效果，节省了开发调试的时间和工作量。"
            />
            <InfoCard
              index={3}
              title="开发者 SDK 支持"
              desc="为了方便开发者集成接口到自己的代码中，平台提供了客户端SDK，使调用接口变得更加简单和便捷。"
            />
          </div>
        </div>
      </Card>
    </PageContainer>
  );
};

export default Welcome;

