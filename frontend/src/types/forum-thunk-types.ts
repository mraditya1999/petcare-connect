import { ApiResponse } from "./api";
import { IForum } from "./forum-types";

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
  likeCount: number;
  commentCount: number;
  likesCount?: number;
  commentsCount?: number;
  createdAt: string;
  updatedAt: string;
  userId: number;
  likedByCurrentUser?: boolean;
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
  updatedAt?: string;
  firstName: string;
  lastName: string;
  email: string;
  parentId: string | null;
  childCount?: number;
  likeCount?: number;
  isEdited?: boolean;
}

export interface ICommentListData {
  content: IComment[];
  pageNumber: number;
  pageSize: number;
  totalElements: number;
  totalPages: number;
  lastPage: boolean;
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
  updatedAt?: string;
  firstName: string;
  lastName: string;
  email: string;
  parentId: string | null;
  childCount?: number;
  likeCount?: number;
  isEdited?: boolean;
}

export interface IUpdateCommentResponse {
  message: string;
  data: IUpdatedComment;
}

export type IDeleteCommentResponse = ApiResponse<null>;

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

export interface IForumListData {
  forums: IForum[];
  pageNumber: number;
  pageSize: number;
  totalElements: number;
  totalPages: number;
  lastPage: boolean;
}

export interface IFetchForumsResponse {
  message: string;
  data: IForumListData;
}

export interface IFetchFeaturedForumsResponse {
  message: string;
  data: IForum[];
}

export interface ICreateForumResponse {
  // no payload returned, just void
}

export interface IUpdateForumParams {
  forumId: string;
  title: string;
  content: string;
  tags: string[];
}

export interface IUpdateForumResponse {
  message: string;
  data: ISingleForum;
}

export interface IDeleteForumParams {
  forumId: string;
}

export interface IDeleteForumResponse {
  message: string;
  data: null;
}
