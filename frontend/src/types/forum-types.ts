import { IPageInfo } from "./pagination-types";

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
// *******************************************************
export interface ISingleForumResponse {
  message: string;
  data: IForum;
}

export interface ILikeCheckResponse {
  message: string;
  data: {
    isLiked: boolean;
  };
}

export interface ICommentsPageData {
  content: IComment[];
  page: IPageInfo;
}

export interface ICommentsResponse {
  message: string;
  data: ICommentsPageData;
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
  likedByUsers: string[];
  replies: IComment[];
}

export interface ICommentResponse {
  message: string;
  data: IComment;
}

export interface ILike {
  likeId: string;
  forumId: string;
  userId: number | string;
  createdAt: string;
}

export interface ILikeResponse {
  message: string;
  data: null | ILike;
}

export interface IForumState {
  forum: IForum | null;
  comments: IComment[];
  commentPage: number;
  totalCommentPages: number;
  loading: boolean;
  error: string | null;
}

export interface CreateCommentParams {
  forumId: string;
  text: string;
}

export interface UpdateCommentParams {
  commentId: string;
  text: string;
}
