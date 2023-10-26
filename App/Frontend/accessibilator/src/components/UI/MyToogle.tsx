import { useState } from 'react';
import { Switch } from '@headlessui/react';

const MyToggle = () => {
  const [enabled, setEnabled] = useState(false);

  return (
    <Switch
      checked={enabled}
      onChange={setEnabled}
      className="relative inline-flex h-6 w-11 items-center rounded-full ui-checked:bg-stone-500 ui-not-checked:bg-stone-400"
    >
      <span className="sr-only">Enable notifications</span>
      <span className="inline-block h-4 w-4 transform rounded-full ui-checked:bg-stone-900 ui-not-checked:bg-stone-100 transition ui-checked:translate-x-6 ui-not-checked:translate-x-1" />
    </Switch>
  );
};

export default MyToggle;
