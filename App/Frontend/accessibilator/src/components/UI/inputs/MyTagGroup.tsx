import { FC, ReactNode } from 'react';
import type { TagGroupProps, TagProps } from 'react-aria-components';
import { Button, Label, Tag, TagGroup, TagList } from 'react-aria-components';

/**
 * Defining the interface for MyTagGroupProps which extends TagGroupProps from react-aria-components.
 * It includes a label and an items array, each item having an id and content.
 */
interface MyTagGroupProps extends TagGroupProps {
  label: ReactNode;
  items: Array<{ id: string; content: ReactNode }>;
}

// MyTagGroup functional component which takes MyTagGroupProps as props.
const MyTagGroup: FC<MyTagGroupProps> = ({
  label,
  items,
  children,
  ...props
}) => {
  return (
    <TagGroup {...props}>
      {/* Rendering the label for the tag group */}
      <Label>{label}</Label>
      {/* TagList to display a list of tags. It has a custom className for styling. */}
      <TagList items={items} className='mt-6 grid grid-cols-4 gap-x-7 gap-y-6'>
        {/* Function to render each item as a MyTag component */}
        {(item) => (
          <MyTag
            key={item.id}
            id={item.id}
            textValue={item.id}
            className={
              // Custom className for MyTag for styling and transitions.
              'relative flex cursor-pointer flex-col rounded border border-gray-700 p-1 ring-yellow-600 ring-offset-2 ring-offset-slate-200 transition focus:outline-none focus-visible:ring-2 focus-visible:ring-yellow-700 selected:border-2 selected:border-yellow-800'
            }
          >
            {/* Rendering the content of each tag */}
            {item.content}
          </MyTag>
        )}
      </TagList>
    </TagGroup>
  );
};

// MyTag functional component which takes TagProps as props.
const MyTag: FC<TagProps> = ({ children, textValue, ...props }) => {
  // Conditional handling of the textValue for the tag, setting it to the default value if not provided.
  let textVal =
    textValue ?? typeof children === 'string' ? `${children}` : undefined;
  return (
    // Using Tag component from react-aria-components and spreading props.
    <Tag textValue={textVal} {...props}>
      {/* Render function for additional elements like remove button and selected state indication */}
      {({ allowsRemoving, isSelected }) => (
        <>
          {children}
          {/* Conditionally rendering a remove button if allowed */}
          {allowsRemoving && <Button slot='remove'>ⓧ</Button>}
          {/* Indicating selected state with a styled div */}
          {isSelected && (
            <div className='absolute -bottom-[0.625rem] left-0 z-10 h-1 w-full rounded bg-yellow-800'></div>
          )}
        </>
      )}
    </Tag>
  );
};

export default MyTagGroup;
