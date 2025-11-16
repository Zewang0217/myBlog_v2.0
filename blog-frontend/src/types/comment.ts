// blog-frontend/src/types/comment.ts
// 评论相关的类型定义

/**
 * 评论数据模型
 */
export interface Comment {
  id: string;
  content: string;
  author: string;
  authorEmail?: string;
  createTime: string;
  updateTime?: string;
  articleId: string;
  parentId?: string;
  replies?: Comment[];
  likeCount: number;
  isLiked?: boolean;
}

/**
 * 创建评论的数据传输对象
 */
export interface CreateCommentDTO {
  content: string;
  author: string;
  authorEmail?: string;
  articleId: string;
  parentId?: string;
}

/**
 * 评论列表响应结构
 */
export interface CommentListResponse {
  code: number;
  message: string;
  data: Comment[];
}

/**
 * 单条评论响应结构
 */
export interface CommentResponse {
  code: number;
  message: string;
  data: Comment;
}

/**
 * 点赞操作响应结构
 */
export interface LikeResponse {
  code: number;
  message: string;
  data: { success: boolean; likeCount: number; isLiked: boolean };
}