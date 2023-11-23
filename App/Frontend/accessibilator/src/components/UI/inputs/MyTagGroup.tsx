import { FC, ReactNode } from 'react';
import type { TagGroupProps, TagProps } from 'react-aria-components';
import { Button, Label, Tag, TagGroup, TagList } from 'react-aria-components';

interface MyTagGroupProps extends TagGroupProps {
  label: ReactNode;
  items: Array<{ id: string; content: ReactNode }>;
}

const MyTagGroup: FC<MyTagGroupProps> = ({
  label,
  items,
  children,
  ...props
}) => {
  return (
    <TagGroup {...props}>
      <Label>{label}</Label>
      <TagList items={items} className='mt-6 grid grid-cols-4 gap-x-7 gap-y-6'>
        {(item) => (
          <MyTag
            key={item.id}
            id={item.id}
            textValue={item.id}
            className={
              'relative flex cursor-pointer flex-col rounded border border-gray-700 p-1 ring-yellow-600 ring-offset-2 ring-offset-slate-200 transition focus:outline-none focus-visible:ring-2 focus-visible:ring-yellow-700 selected:border-2 selected:border-yellow-800'
            }
          >
            {item.content}
          </MyTag>
        )}
      </TagList>
    </TagGroup>
  );
};

const MyTag: FC<TagProps> = ({ children, textValue, ...props }) => {
  let textVal =
    textValue ?? typeof children === 'string' ? `${children}` : undefined;
  return (
    <Tag textValue={textVal} {...props}>
      {({ allowsRemoving, isSelected }) => (
        <>
          {children}
          {allowsRemoving && <Button slot='remove'>ⓧ</Button>}
          {isSelected && (
            <div className='absolute -bottom-[0.625rem] left-0 z-10 h-1 w-full rounded bg-yellow-800'></div>
          )}
        </>
      )}
    </Tag>
  );
};

export default MyTagGroup;
