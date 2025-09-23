export interface Article {
  id: number
  title: string
  content: string
  author: string
  createTime: string
  updateTime: string
  status: number
}

export interface CreateArticleDTO {
  title: string
  content: string
  author: string
}
