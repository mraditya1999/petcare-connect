import { useEffect } from "react";
import { useAppDispatch, useAppSelector } from "@/app/hooks";
import { fetchMyForums } from "@/features/forumList/forumListThunk";
import { setMyForumsPage } from "@/features/forumList/forumListSlice";
import { ForumListContainer } from "@/components";

const ForumsTab: React.FC = () => {
  const dispatch = useAppDispatch();
  const {
    myForums,
    loading,
    error,
    myForumsPage,
    size,
    myForumsTotalPages,
    myForumsTotalElements,
  } =
    useAppSelector((state) => state.forumList);

  useEffect(() => {
    dispatch(fetchMyForums({ page: myForumsPage, size }));
  }, [dispatch, myForumsPage, size]);

  useEffect(() => {
    if (loading) return;
    if (error) return;
    if (myForumsPage <= 0) return;
    if ((myForums?.length ?? 0) > 0) return;
    if (myForumsTotalElements <= 0) return;

    dispatch(setMyForumsPage(myForumsPage - 1));
  }, [dispatch, loading, error, myForumsPage, myForums, myForumsTotalElements]);

  return (
    <ForumListContainer
      title="My Forums"
      forums={myForums}
      loading={loading}
      error={error}
      page={myForumsPage}
      totalPages={myForumsTotalPages}
      onPageChange={(p) => dispatch(setMyForumsPage(p))}
      onRetry={() => dispatch(fetchMyForums({ page: myForumsPage, size }))}
      emptyMessage="You haven’t created any forums yet."
    />
  );
};

export default ForumsTab;
