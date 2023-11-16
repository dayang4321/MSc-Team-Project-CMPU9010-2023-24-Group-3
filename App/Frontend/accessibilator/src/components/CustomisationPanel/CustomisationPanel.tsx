import React, { useState } from 'react';
import { Tab, TabList, TabPanel, Tabs } from 'react-aria-components';
import type { TabPanelProps, TabProps } from 'react-aria-components';
import MySlider from '../UI/inputs/MySlider';
import MySelect from '../UI/inputs/MySelect';
import InfoTooltip from '../InfoTooltip/InfoTooltip';
import MyToggle from '../UI/MyToggle';
import MyRadioGroup from '../UI/inputs/MyRadioGroup';
import {
  MdFormatAlignLeft,
  MdFormatAlignRight,
  MdFormatAlignCenter,
} from 'react-icons/md';
import Button from '../UI/Button';

const fontStyleOptions: Array<{
  id: DocModifyParams['fontType'];
  name: string;
}> = [
  { id: 'arial', name: 'Arial' },
  { id: 'comicSans', name: 'Comic Sans' },
  { id: 'openDyslexic', name: 'Open Dyslexic' },
  { id: 'helvetica', name: 'Helvetica' },
  { id: 'lexend', name: 'Lexend' },
  { id: 'openSans', name: 'Open Sans' },
] as const;

type CustomisationPanelProps = {
  docConfigData: DocumentData['documentConfig'];
  onConfigSave: (data: DocModifyParams) => void;
  configSaveLoading: boolean;
};

const CustomisationPanel = ({
  docConfigData,
  configSaveLoading,
  onConfigSave,
}: CustomisationPanelProps) => {
  const [modificationsObj, setModificationsObj] = useState<DocModifyParams>({
    fontSize: Number(docConfigData.fontSize),
    lineSpacing: Number(docConfigData.lineSpacing),
    characterSpacing: Number(docConfigData.characterSpacing) / 10,
    fontType: docConfigData.fontType,
    alignment: docConfigData.alignment,
    removeItalics: docConfigData.removeItalics,
    generateTOC: docConfigData.generateTOC,
    backgroundColor: docConfigData.backgroundColor,
    fontColor: docConfigData.fontColor,
  });

  const modObjHandler = <T extends keyof DocModifyParams>(
    key: T,
    value: DocModifyParams[T]
  ) => {
    setModificationsObj((s) => {
      return {
        ...s,
        [key]: value,
      };
    });
  };

  const valInPixels = (val: number) => `${val}px`;

  return (
    <div className='flex min-h-0 flex-1 flex-col overflow-hidden'>
      <Tabs className='flex  min-h-0 w-full flex-1 flex-col overflow-y-hidden'>
        <TabList
          aria-label='Customisation Panel'
          className='shadow-bttm flex space-x-8 bg-clip-padding'
        >
          <MyTab id='text'>Text</MyTab>
          <MyTab id='colour'>Colour</MyTab>
          <MyTab id='extra'>Extra</MyTab>
        </TabList>
        <MyTabPanel id='text'>
          <div className='flex flex-col space-y-4 divide-y divide-gray-300 '>
            <div className='px-16 py-4'>
              <MySelect
                selectedKey={modificationsObj.fontType}
                label='Font Style'
                items={fontStyleOptions}
                onSelectionChange={(key: DocModifyParams['fontType']) => {
                  modObjHandler('fontType', key);
                }}
              />
            </div>
            <div className='px-16 py-4'>
              <MySlider<number>
                minValue={11}
                maxValue={21}
                step={1}
                value={modificationsObj.fontSize}
                onChange={(val) => modObjHandler('fontSize', val)}
                outputValFormat={valInPixels}
                label='Font Size'
              />
            </div>
            <div className='px-16 py-4'>
              <MySlider
                minValue={1}
                defaultValue={1.5}
                maxValue={3}
                step={0.25}
                value={modificationsObj.lineSpacing}
                onChange={(val) => modObjHandler('lineSpacing', val)}
                label='Line Spacing'
              />
            </div>
            <div className='px-16 py-4'>
              <MySlider
                minValue={0}
                maxValue={1}
                formatOptions={{
                  style: 'percent',
                }}
                step={0.25}
                defaultValue={0.25}
                value={modificationsObj.characterSpacing}
                onChange={(val) => modObjHandler('characterSpacing', val)}
                label='Letter Spacing'
              />
            </div>
            <div className='px-16 py-6 pb-0'>
              <div className='flex flex-col items-center justify-between'>
                <MyRadioGroup
                  value={modificationsObj.alignment}
                  onChange={(val: typeof modificationsObj.alignment) => {
                    modObjHandler('alignment', val);
                  }}
                  label={
                    <InfoTooltip infoTip="We've aligned the text to the left without justification. This alignment helps in maintaining a consistent visual flow, making it easier to find the start and finish of each line. It also ensures even spacing between words.">
                      <p>Adjust Alignment</p>
                    </InfoTooltip>
                  }
                  options={[
                    {
                      name: 'LEFT',
                      content: <MdFormatAlignLeft className='h-6 w-6' />,
                    },
                    {
                      name: 'CENTRE',
                      content: <MdFormatAlignCenter className='h-6 w-6' />,
                    },
                    {
                      name: 'RIGHT',
                      content: <MdFormatAlignRight className='h-6 w-6' />,
                    },
                  ]}
                />
              </div>
            </div>
            <div className='px-16 py-6'>
              <div className='flex items-center justify-between'>
                <InfoTooltip infoTip='We try to avoid italics as much as possible, as they can cause letters to appear connected and crowded, which can be challenging for readers with dyslexia. Bold text will used for emphasis instead'>
                  <p>Remove Italics</p>
                </InfoTooltip>
                <MyToggle
                  ariaLabel='Remove Italics'
                  checked={modificationsObj.removeItalics}
                  onChange={(checked) =>
                    modObjHandler('removeItalics', checked)
                  }
                />
              </div>
            </div>
          </div>
        </MyTabPanel>
        <MyTabPanel id='colour'>
          <div className='flex flex-col'></div>
        </MyTabPanel>
        <MyTabPanel id='extra'>
          <div className='flex flex-col'></div>
        </MyTabPanel>
      </Tabs>
      <div className='border px-16 py-8 '>
        <Button
          className=' px-6 py-2 text-base'
          loading={configSaveLoading}
          text={'Save'}
          onClick={() => {
            onConfigSave(modificationsObj);
          }}
        />
      </div>
    </div>
  );
};

export default CustomisationPanel;

function MyTab(props: TabProps) {
  return (
    <Tab
      {...props}
      className={({ isSelected }) => `
        w-full cursor-pointer py-2.5 text-center text-lg font-medium outline-none ring-yellow-700 transition-colors focus-visible:ring-2
        ${
          isSelected
            ? 'border-b-2 border-b-yellow-800 bg-white text-yellow-800'
            : 'pressed:bg-yellow-600/10 hover:bg-yellow-600/10'
        }
      `}
    />
  );
}

function MyTabPanel(props: TabPanelProps) {
  return (
    <TabPanel
      className='mt-2 min-h-0 flex-1 overflow-auto p-0 pb-8 outline-none ring-yellow-700 focus-visible:ring-2'
      {...props}
    />
  );
}
