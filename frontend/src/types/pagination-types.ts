export interface IPageInfo {
  size: number;
  number: number;
  totalElements: number;
  totalPages: number;
}

export interface IPageResponse<T> {
  content: T[];
  page: IPageInfo;
}
