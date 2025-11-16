export interface Article {
  id: string
  title: string
  content: string
  author: string
  createTime: string
  updateTime: string
  status: number | string  // 支持数字和字符串两种格式
  categories?: Category[]
  likeCount: number
  isLiked?: boolean
  coverImage?: string  // 封面图片URL
}

export interface CreateArticleDTO {
  title: string
  content: string
  author: string
  status?: number
  categoryIds?: string[]
  coverImage?: string  // 封面图片URL
}

export enum ArticleStatus {
  DRAFT = 0,
  PUBLISHED = 1,
  OFFLINE = 2
}

export interface Category {
  id: string;
  name: string;
  description: string;
  createTime: string;
  updateTime: string;
  articleCount?: number;
}

export interface ArticleCategory {
  id: string;
  articleId: string;
  categoryId: string;
}