import {
  Button,
  FieldError,
  Label,
  ListBox,
  ListBoxItem,
  Popover,
  Select,
  SelectValue,
  Text,
} from 'react-aria-components';
import type { ListBoxItemProps } from 'react-aria-components';
import { ChevronDownIcon } from '@heroicons/react/20/solid';
import { MySelectProps } from './interfaces';

function MySelect<
  T extends {
    id: string;
    name: string;
  },
>({ label, description, errorMessage, items, ...props }: MySelectProps<T>) {
  return (
    <Select className='flex flex-col gap-2' {...props}>
      <Label className='cursor-default'>{label}</Label>
      <Button className='flex cursor-default items-center rounded-md border border-slate-400 bg-white bg-opacity-90 py-2 pl-4 pr-2 text-left text-base leading-normal text-gray-700 shadow ring-yellow-600 ring-offset-2 ring-offset-slate-200 transition focus:outline-none focus-visible:ring-2 pressed:bg-opacity-100 disabled:bg-gray-400/60'>
        <SelectValue className='flex-1 truncate  placeholder-shown:text-slate-500' />
        <ChevronDownIcon
          className='h-5 w-5 text-slate-500'
          aria-hidden='true'
        />
      </Button>
      {description && <Text slot='description'>{description}</Text>}
      <FieldError>{errorMessage}</FieldError>
      <Popover className='entering:animate-in entering:fade-in exiting:animate-out exiting:fade-out max-h-60 w-[--trigger-width] overflow-auto rounded-md bg-white text-base shadow-lg ring-1 ring-black/5'>
        <ListBox className='p-1 outline-none' items={items}>
          {(
            item: T & {
              id: string;
              name: string;
            }
          ) => (
            <MyItem key={item.id} id={item.id}>
              {item.name}
            </MyItem>
          )}
        </ListBox>
      </Popover>
    </Select>
  );
}

export function MyItem(props: ListBoxItemProps) {
  return (
    <ListBoxItem
      {...props}
      className='group flex cursor-default select-none items-center gap-2 rounded px-4 py-2 text-gray-900 outline-none focus:bg-yellow-800 focus:text-slate-50'
    />
  );
}

export default MySelect;
