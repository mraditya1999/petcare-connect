import { IForum, IPaginationInfo } from "./forum-types";

// ---------- Request Params ----------
export interface IFetchSingleForumParams {
  forumId: string;
}

export interface IFetchCommentsParams {
  forumId: string;
  page?: number;
  size?: number;
}

export interface ICreateCommentParams {
  forumId: string;
  text: string;
}

export interface IUpdateCommentParams {
  commentId: string;
  text: string;
}

export interface IDeleteCommentParams {
  commentId: string;
}

export interface ICheckLikeParams {
  forumId: string;
}

export interface IToggleLikeParams {
  forumId: string;
}

export interface IFetchForumsParams {
  page: number;
  size: number;
  sortBy: string;
  sortDir: "asc" | "desc";
  searchTerm: string;
  tagSearchTerm: string;
}

export interface ICreateForumParams {
  title: string;
  content: string;
  tags: string[];
}

// ---------- Response Types ----------
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

export interface IFetchSingleForumResponse {
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

export interface IFetchCommentsResponse {
  message: string;
  data: ICommentListData;
}

export interface ICreateCommentResponse {
  message: string;
  data: IComment;
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
}

export interface IUpdateCommentResponse {
  message: string;
  data: IUpdatedComment;
}

export interface IDeleteCommentResponse {
  commentId: string;
}

export interface ICheckLikeResponse {
  message: string;
  data: {
    isLiked: boolean;
  };
}

export interface IToggleLikeResponse {
  message: string;
  data: null;
}

export interface IFetchForumsResponse {
  content: IForum[];
  page: IPaginationInfo;
}

export interface IFetchFeaturedForumsResponse {
  message: string;
  data: IForum[];
}

export interface ICreateForumResponse {
  // no payload returned, just void
}
