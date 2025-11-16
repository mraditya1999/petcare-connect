export interface IForum {
  forumId: string;
  title: string;
  content: string;
  tags: string[];
  firstName: string;
  lastName: string;
  email: string;
  likesCount: number;
  commentsCount: number;
  createdAt: string;
  updatedAt: string;
}

export interface IPaginationInfo {
  size: number;
  number: number;
  totalElements: number;
  totalPages: number;
}

export interface IForumListResponse {
  content: IForum[];
  page: IPaginationInfo;
}

export interface IFeaturedForumResponse {
  message: string;
  data: IForum[];
}

export interface ISingleForum {
  forumId: string;
  title: string;
  content: string;
  tags: string[];
  firstName: string;
  lastName: string;
  email: string;
  likesCount: number;
  commentsCount: number;
  createdAt: string;
  updatedAt: string;
}

export interface ISingleForumResponse {
  message: string;
  data: ISingleForum;
}

export interface IComment {
  commentId: string;
  forumId: string;
  userId: number;
  text: string;
  createdAt: string;
  firstName: string;
  lastName: string;
  email: string;
  parentId: string | null;
  likedByUsers: number[];
  replies: IComment[];
}

export interface ICommentPageInfo {
  size: number;
  number: number;
  totalElements: number;
  totalPages: number;
}

export interface ICommentListData {
  content: IComment[];
  page: ICommentPageInfo;
}

export interface ICommentListResponse {
  message: string;
  data: ICommentListData;
}

export interface ILikeCheckResponse {
  message: string;
  data: {
    isLiked: boolean;
  };
}

export interface IAddCommentResponse {
  message: string;
  data: IComment;
}

export interface IUpdateCommentResponse {
  message: string;
  data: IUpdatedComment;
}

export interface IUpdatedComment {
  commentId: string;
  forumId: string;
  userId: number;
  text: string;
  createdAt: string;

  firstName: string;
  lastName: string;
  email: string;

  parentId: string | null;
  // likedByUsers: any[];
  // replies: any[];
}

export interface IToggleLikeResponse {
  message: string;
  data: null;
}

export interface ICheckLikeData {
  isLiked: boolean;
}

export interface ICheckLikeResponse {
  message: string;
  data: ICheckLikeData;
}
