export interface Article {
  id: number
  title: string
  content: string
  author: string
  createTime: string
  updateTime: string
  status: number
  categories?: Category[]
}

export interface CreateArticleDTO {
  title: string
  content: string
  author: string
  status?: number
  categoryIds?: number[]
}

export enum ArticleStatus {
  DRAFT = 0,
  PUBLISHED = 1,
  OFFLINE = 2
}

export interface Category {
  id: number;
  name: string;
  description: string;
  createTime: string;
  updateTime: string;
}

export interface ArticleCategory {
  id: number;
  articleId: number;
  categoryId: number;
}