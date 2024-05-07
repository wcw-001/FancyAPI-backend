// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';

/** upload POST /api/file/upload */
export async function uploadUsingPOST(body: string, options?: { [key: string]: any }) {
  return request<API.BaseResponseMapstringobject>('/api/file/upload', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}
