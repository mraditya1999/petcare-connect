import { Button } from "../ui/button";
import { Input } from "../ui/input";
import { ForumEditor } from "@/components";

interface ICreateForumSectionProps {
  title: string;
  tags: string[];
  content: string;
  setTitle: (val: string) => void;
  setTags: (val: string[]) => void;
  setContent: (val: string) => void;
  onCreate: () => void;
  loading: boolean;
  errors?: { title?: string; tags?: string; content?: string };
  setErrors: React.Dispatch<
    React.SetStateAction<{ title?: string; tags?: string; content?: string }>
  >;
}
const CreateForumSection: React.FC<ICreateForumSectionProps> = ({
  title,
  tags,
  content,
  setTitle,
  setTags,
  setContent,
  onCreate,
  loading,
  errors = {},
  setErrors,
}) => (
  <section className="bg-white py-16 dark:bg-gray-900">
    <div className="section-width mx-auto px-6">
      <h2 className="mb-4 text-xl font-semibold text-gray-900 dark:text-gray-100">
        Create a New Forum
      </h2>
      <Input
        placeholder="Forum Title"
        value={title}
        onChange={(e) => {
          setTitle(e.target.value);
          if (errors.title)
            setErrors((prev) => ({ ...prev, title: undefined }));
        }}
      />
      {errors.title && <p className="text-sm text-red-500">{errors.title}</p>}

      <Input
        placeholder="Tags (comma-separated)"
        value={tags.join(",")}
        onChange={(e) => {
          setTags(e.target.value.split(",").map((t) => t.trim()));
          if (errors.tags) setErrors((prev) => ({ ...prev, tags: undefined }));
        }}
      />
      {errors.tags && <p className="text-sm text-red-500">{errors.tags}</p>}

      <ForumEditor
        value={content}
        onChange={(val) => {
          setContent(val);
          if (errors.content)
            setErrors((prev) => ({ ...prev, content: undefined }));
        }}
      />
      {errors.content && (
        <p className="text-sm text-red-500">{errors.content}</p>
      )}

      <Button
        onClick={onCreate}
        className="mt-4 bg-primary text-white hover:bg-primary/90 dark:bg-gray-200 dark:text-gray-900 dark:hover:bg-gray-300"
      >
        {loading ? "Creating..." : "Create Forum"}
      </Button>
    </div>
  </section>
);

export default CreateForumSection;
