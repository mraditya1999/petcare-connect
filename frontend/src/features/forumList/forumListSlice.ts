import { createSlice, PayloadAction } from "@reduxjs/toolkit";
import { IForumListState } from "@/types/forum-types";
import { IFetchFeaturedForumsResponse } from "@/types/forum-thunk-types";
import {
  fetchForums,
  fetchFeaturedForums,
  createForum,
  fetchMyForums,
} from "./forumListThunk";

const initialState: IForumListState = {
  forums: [],
  featuredForums: [],
  myForums: [],
  page: 0,
  size: 5,
  totalPages: 0,
  totalElements: 0,
  sortBy: "createdAt",
  sortDir: "desc",
  searchTerm: "",
  tagSearchTerm: "",
  loading: false,
  error: null,
  featuredLoading: false,
  featuredError: null,
  myForumsPage: 0,
  myForumsTotalPages: 0,
  myForumsTotalElements: 0,
  myForumsLoading: false,
  myForumsError: null,
};

const forumListSlice = createSlice({
  name: "forumList",
  initialState,
  reducers: {
    setPage(state, action: PayloadAction<number>) {
      state.page = action.payload;
    },
    setSortBy(state, action: PayloadAction<string>) {
      state.sortBy = action.payload;
    },
    setSortDir(state, action: PayloadAction<"asc" | "desc">) {
      state.sortDir = action.payload;
    },
    setSearchTerm(state, action: PayloadAction<string>) {
      state.searchTerm = action.payload;
      state.page = 0;
    },
    setTagSearchTerm(state, action: PayloadAction<string>) {
      state.tagSearchTerm = action.payload;
      state.page = 0;
    },
    setSize(state, action: PayloadAction<number>) {
      state.size = action.payload;
    },
    clearError(state) {
      state.error = null;
    },
    setMyForumsPage(state, action: PayloadAction<number>) {
      state.myForumsPage = action.payload;
    },
  },
  extraReducers: (builder) => {
    builder
      // Fetch Forums
      .addCase(fetchForums.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchForums.fulfilled, (state, action) => {
        state.loading = false;
        state.forums = action.payload.data.content;

        const totalElements =
          (action.payload.data as unknown as { totalElements?: number; page?: { totalElements?: number } })
            .totalElements ??
          (action.payload.data as unknown as { page?: { totalElements?: number } }).page
            ?.totalElements ??
          0;

        const totalPages =
          (action.payload.data as unknown as { totalPages?: number; page?: { totalPages?: number } })
            .totalPages ??
          (action.payload.data as unknown as { page?: { totalPages?: number } }).page
            ?.totalPages ??
          1;

        state.totalElements = totalElements;
        state.totalPages = totalElements > state.size ? totalPages : 1;
      })
      .addCase(fetchForums.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload || "Failed to load forums";
      });

    // Featured Forums
    builder
      .addCase(fetchFeaturedForums.pending, (state) => {
        state.featuredLoading = true;
        state.featuredError = null;
      })
      .addCase(
        fetchFeaturedForums.fulfilled,
        (state, action: PayloadAction<IFetchFeaturedForumsResponse>) => {
          state.featuredLoading = false;
          state.featuredForums = action.payload.data;
        },
      )
      .addCase(fetchFeaturedForums.rejected, (state, action) => {
        state.featuredLoading = false;
        state.featuredError =
          action.error.message || "Failed to load featured forums";
      });
    // Create Forum
    builder
      .addCase(createForum.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(createForum.fulfilled, (state) => {
        state.loading = false;
      })
      .addCase(createForum.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload || "Failed to create forum";
      });
    builder
      .addCase(fetchMyForums.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchMyForums.fulfilled, (state, action) => {
        state.loading = false;
        state.myForums = action.payload.data.content;

        const totalElements =
          (action.payload.data as unknown as { totalElements?: number; page?: { totalElements?: number } })
            .totalElements ??
          (action.payload.data as unknown as { page?: { totalElements?: number } }).page
            ?.totalElements ??
          0;

        const totalPages =
          (action.payload.data as unknown as { totalPages?: number; page?: { totalPages?: number } })
            .totalPages ??
          (action.payload.data as unknown as { page?: { totalPages?: number } }).page
            ?.totalPages ??
          1;

        state.myForumsTotalElements = totalElements;
        state.myForumsTotalPages = totalElements > state.size ? totalPages : 1;
      })
      .addCase(fetchMyForums.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload || "Failed to load my forums";
      });
  },
});

export const {
  setPage,
  setSortBy,
  setSortDir,
  setSearchTerm,
  setTagSearchTerm,
  setSize,
  clearError,
  setMyForumsPage,
} = forumListSlice.actions;

export default forumListSlice.reducer;
