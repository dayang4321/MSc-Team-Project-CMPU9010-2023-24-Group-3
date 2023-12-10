// Importing necessary React components and custom UI components
import React, { useState } from 'react';
import { TabList, Tabs } from 'react-aria-components';
import MySlider from '../UI/inputs/MySlider';
import MySelect from '../UI/inputs/MySelect';
import InfoTooltip from '../InfoTooltip/InfoTooltip';
import MyToggle from '../UI/MyToggle';
import MyRadioGroup from '../UI/inputs/MyRadioGroup';
import Button from '../UI/Button';
import {
  ALIGNMENT_OPTIONS,
  FONT_STYLE_OPTIONS,
  THEME_MAP,
} from '../../configs/selectOptions';
import { MyTab, MyTabPanel } from '../UI/tabs';
import MyTagGroup from '../UI/inputs/MyTagGroup';
import { findKey } from 'lodash';

// Define the type for the props of CustomisationPanel component
type CustomisationPanelProps = {
  customisationConfig: DocModifyParams;
  onConfigSave: (docParamData: DocModifyParams) => void;
  configSaveLoading: boolean;
};

// CustomisationPanel component definition
const CustomisationPanel = ({
  customisationConfig,
  configSaveLoading,
  onConfigSave,
}: CustomisationPanelProps) => {
  // Initializing state for the document configuration and custom theme colors
  const docConfigData = { ...customisationConfig };

  // State to manage the modifications made by the user
  const [modificationsObj, setModificationsObj] = useState<DocModifyParams>({
    // Initial values are set based on the provided configuration
    fontSize: Number(docConfigData?.fontSize),
    lineSpacing: Number(docConfigData?.lineSpacing),
    characterSpacing: Number(docConfigData?.characterSpacing) / 10,
    // Other configuration settings
    fontType: docConfigData?.fontType,
    alignment: docConfigData?.alignment,
    removeItalics: docConfigData?.removeItalics,
    generateTOC: docConfigData?.generateTOC,
    backgroundColor: docConfigData?.backgroundColor,
    fontColor: docConfigData?.fontColor,
  });

  // State to toggle theme customisation
  const [isCustomisingTheme, setIsCustomisingTheme] = useState(false);

  // Determine initial custom theme based on the document configuration
  const initialCustomTheme =
    docConfigData?.backgroundColor &&
    docConfigData?.fontColor &&
    getSelectedKey(
      docConfigData.backgroundColor,
      docConfigData.fontColor
    )[0] === 'custom'
      ? {
          bgColor: docConfigData?.backgroundColor,
          textColor: docConfigData.fontColor,
        }
      : {
          bgColor: null,
          textColor: null,
        };

  // State for managing custom theme colors
  const [customThemeColors, setCustomThemeColors] =
    useState(initialCustomTheme);

  // Handler to update configuration settings
  const changeConfigHandler = <T extends keyof DocModifyParams>(
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

  // Handler to change the theme
  const changeThemeHandler = (key: keyof typeof THEME_MAP | 'custom') => {
    // Update the state based on the selected theme
    if (key === 'custom') {
      setModificationsObj((s) => {
        return {
          ...s,
          backgroundColor: customThemeColors.bgColor,
          fontColor: customThemeColors.textColor,
        };
      });
    } else {
      setModificationsObj((s) => {
        return {
          ...s,
          backgroundColor: THEME_MAP[key]?.bgColor || 'FFFFFF',
          fontColor: THEME_MAP[key]?.textColor || '000000',
        };
      });
    }
  };

  // Handler to reset the theme to default
  const resetThemeHandler = () => {
    setModificationsObj((s) => {
      return {
        ...s,
        backgroundColor: null,
        fontColor: null,
      };
    });
  };

  // Function to convert value to pixels for display
  const valInPixels = (val: number) => `${val}px`;

  // Function to get the selected theme key based on the current colors
  function getSelectedKey(currBgColor: string, currTextColor: string) {
    const themeKey = findKey(THEME_MAP, {
      bgColor: currBgColor,
      textColor: currTextColor,
    });

    const selectedKey = themeKey ? [themeKey] : ['custom'];

    return selectedKey;
  }

  // Preparing theme tag options for display
  const themeTagOptions = Object.entries({
    ...THEME_MAP,
    ...(customThemeColors.bgColor &&
      customThemeColors.textColor && {
        custom: customThemeColors,
      }),
  }).map(([id, { bgColor, textColor }], idx) => {
    return {
      id,
      content: (
        <span
          key={idx}
          style={{
            backgroundColor: `#${bgColor}`,
            color: `#${textColor}`,
          }}
          className='inline-block flex-1 self-stretch  border border-gray-400 px-4 py-7 text-center  font-medium shadow-md'
        >
          Aa
        </span>
      ),
    };
  });

  /**
   * The return statement contains the JSX for rendering the CustomisationPanel
   * It includes tabs for different customisation options like Text, Colour, and Special
   * Each tab panel contains various UI elements like sliders, toggles, and selectors
   * to modify document properties like font size, color theme, etc.
   */
  return (
    // The main container for the CustomisationPanel
    <div className='flex min-h-0 flex-1 flex-col overflow-hidden'>
      {/* Tabs for different sections of the customisation panel */}
      <Tabs
        className='flex  min-h-0 w-full flex-1 flex-col overflow-x-hidden overflow-y-hidden'
        // selectedKey={'colour'}
      >
        {/* TabList contains individual tabs */}
        <TabList
          aria-label='Customisation Panel'
          className='flex space-x-8 bg-clip-padding shadow-bttm'
        >
          {/* Tabs for selecting different customisation categories */}
          <MyTab id='text'>Text</MyTab>
          <MyTab id='colour'>Colour</MyTab>
          <MyTab id='special'>Special</MyTab>
        </TabList>
        {/* Tab Panels for different categories */}
        <MyTabPanel id='text'>
          {/* Text customisation options */}
          {/* Each option is enclosed in a div and contains sliders, toggles, etc. */}
          {/* The options include font style, size, line and character spacing, alignment, and italics removal */}
          <div className='flex flex-col space-y-4 divide-y divide-gray-300 '>
            <div className='relative px-16 pb-3 pt-8'>
              <div className='absolute right-14 top-3'>
                <MyToggle
                  ariaLabel='Toggle font style modification'
                  checked={!!modificationsObj?.fontType}
                  onChange={(checked) => {
                    checked
                      ? changeConfigHandler('fontType', 'arial')
                      : changeConfigHandler('fontType', null);
                  }}
                />
              </div>

              <MySelect
                selectedKey={modificationsObj?.fontType || 'arial'}
                label={
                  <InfoTooltip
                    position='bottom'
                    infoTip='Use sans serif fonts like Arial or Comic Sans as they appear less crowded, making each letter more distinct and easier to read for people living with dyslexia.'
                  >
                    <p>Font style</p>
                  </InfoTooltip>
                }
                items={FONT_STYLE_OPTIONS}
                isDisabled={!modificationsObj?.fontType}
                onSelectionChange={(
                  key: NonNullable<DocModifyParams['fontType']>
                ) => {
                  changeConfigHandler('fontType', key || 'arial');
                }}
              />
            </div>
            <div className='relative px-16 pb-3 pt-12'>
              <div className='absolute right-14 top-4'>
                <MyToggle
                  ariaLabel='Toggle font size modification'
                  checked={!!modificationsObj?.fontSize}
                  onChange={(checked) => {
                    checked
                      ? changeConfigHandler('fontSize', 12)
                      : changeConfigHandler('fontSize', null);
                  }}
                />
              </div>
              <MySlider<number>
                minValue={11}
                maxValue={21}
                isDisabled={!modificationsObj?.fontSize}
                step={1}
                value={modificationsObj?.fontSize || 12}
                onChange={(val) => changeConfigHandler('fontSize', val)}
                outputValFormat={valInPixels}
                label={
                  <InfoTooltip
                    position='bottom'
                    infoTip='Larger font sizes (12-14 pt.) make for easier reading, especially for readers who may find smaller text challenging to follow'
                  >
                    <p>Font size</p>
                  </InfoTooltip>
                }
              />
            </div>
            <div className='relative px-16 pb-3 pt-12'>
              <div className='absolute right-14 top-4'>
                <MyToggle
                  ariaLabel='Toggle line spacing modification'
                  checked={!!modificationsObj?.lineSpacing}
                  onChange={(checked) => {
                    checked
                      ? changeConfigHandler('lineSpacing', 1.5)
                      : changeConfigHandler('lineSpacing', null);
                  }}
                />
              </div>
              <MySlider
                minValue={1}
                defaultValue={1.5}
                maxValue={3}
                step={0.25}
                isDisabled={!modificationsObj?.lineSpacing}
                value={modificationsObj.lineSpacing || 1.5}
                onChange={(val) => changeConfigHandler('lineSpacing', val)}
                label={
                  <InfoTooltip
                    position='bottom'
                    infoTip='The recommended line spacing (1.5) improves text clarity and reduces visual stress, making it easier for readers to follow lines of text.'
                  >
                    <p>Line spacing</p>
                  </InfoTooltip>
                }
              />
            </div>
            <div className='relative px-16 pb-3 pt-12'>
              <div className='absolute right-14 top-4'>
                <MyToggle
                  ariaLabel='Toggle letter spacing modification'
                  checked={!!modificationsObj?.characterSpacing}
                  onChange={(checked) => {
                    checked
                      ? changeConfigHandler('characterSpacing', 0.25)
                      : changeConfigHandler('characterSpacing', null);
                  }}
                />
              </div>
              <MySlider
                minValue={0.05}
                isDisabled={!modificationsObj?.characterSpacing}
                maxValue={1}
                formatOptions={{
                  style: 'percent',
                }}
                step={0.05}
                defaultValue={0.25}
                value={modificationsObj.characterSpacing ?? 0.25}
                onChange={(val) => changeConfigHandler('characterSpacing', val)}
                label={
                  <InfoTooltip
                    position='bottom'
                    infoTip='Increase the space between letters (around 35% of the average letter width) leads to a more readable text by reducing visual crowding, a common issue for those with dyslexia.'
                  >
                    <p>Letter spacing</p>
                  </InfoTooltip>
                }
              />
            </div>
            <div className='relative px-16 py-6 pb-0'>
              <div className='absolute right-14 top-4'>
                <MyToggle
                  ariaLabel='Toggle letter spacing modification'
                  checked={!!modificationsObj?.alignment}
                  onChange={(checked) => {
                    checked
                      ? changeConfigHandler('alignment', 'LEFT')
                      : changeConfigHandler('alignment', null);
                  }}
                />
              </div>
              <div className='flex flex-col items-center justify-between'>
                <MyRadioGroup
                  value={modificationsObj?.alignment || 'LEFT'}
                  isDisabled={!modificationsObj?.alignment}
                  onChange={(
                    val: NonNullable<typeof modificationsObj.alignment>
                  ) => {
                    changeConfigHandler('alignment', val);
                  }}
                  label={
                    <InfoTooltip
                      position='top'
                      infoTip='Align the text to the left without justification. This alignment helps in maintaining a consistent visual flow, making it easier to find the start and finish of each line. It also ensures even spacing between words.'
                    >
                      <p>Adjust Alignment</p>
                    </InfoTooltip>
                  }
                  options={ALIGNMENT_OPTIONS}
                />
              </div>
            </div>
            <div className='px-16 py-6'>
              <div className='flex items-center justify-between'>
                <InfoTooltip
                  position='top'
                  infoTip='Try to avoid italics as much as possible, as they can cause letters to appear connected and crowded, which can be challenging for readers with dyslexia. Bold text will used for emphasis instead'
                >
                  <p>Remove Italics</p>
                </InfoTooltip>
                <MyToggle
                  ariaLabel='Remove Italics'
                  checked={!!modificationsObj?.removeItalics}
                  onChange={(checked) =>
                    changeConfigHandler('removeItalics', checked)
                  }
                />
              </div>
            </div>
          </div>
        </MyTabPanel>
        <MyTabPanel id='colour'>
          {/* Colour customisation options */}
          {/* Options to set text and background color, including a custom theme creator */}
          <div className='flex flex-col space-y-4 divide-y divide-gray-300'>
            <div className='relative px-16 pb-3 pt-12'>
              <div className='absolute right-14 top-4'>
                <MyToggle
                  ariaLabel='Toggle Text and Background Colour Setting'
                  checked={!!modificationsObj?.backgroundColor}
                  onChange={(checked) => {
                    if (checked) {
                      changeThemeHandler('black_on_white');
                    } else {
                      resetThemeHandler();
                    }
                  }}
                />
              </div>
              <div>
                <MyTagGroup
                  label={
                    <InfoTooltip
                      position='bottom'
                      infoTip='High contrast between text and background minimizes visual strain and enhances visibility of characters, particularly important for individuals with reading and/or visual difficulties'
                    >
                      <p>Theme & Contrast</p>
                    </InfoTooltip>
                  }
                  disallowEmptySelection={true}
                  selectedKeys={getSelectedKey(
                    modificationsObj?.backgroundColor || 'FFFFFF',
                    modificationsObj?.fontColor || '000000'
                  )}
                  selectionMode='single'
                  onSelectionChange={(keys: Set<keyof typeof THEME_MAP>) => {
                    const selectedKey = Array.from(keys)[0];
                    changeThemeHandler(selectedKey);
                  }}
                  items={themeTagOptions}
                />

                <div className='mt-8'>
                  <div className='flex gap-8'>
                    {!isCustomisingTheme && (
                      <Button
                        variant='link'
                        text='Create Theme'
                        className='underline'
                        onClick={() => {
                          setIsCustomisingTheme(true);
                        }}
                      />
                    )}
                  </div>
                  {isCustomisingTheme && (
                    <>
                      <div className='my-5 mb-12 flex gap-8'>
                        <div className='flex flex-row-reverse items-center gap-3'>
                          <label
                            aria-description='Custom Text Color'
                            htmlFor='custom_text'
                          >
                            Text
                          </label>
                          <div className='rounded border border-gray-400 p-1 pb-0'>
                            <input
                              type='color'
                              id='custom_text'
                              value={`#${
                                customThemeColors?.textColor || '000000'
                              }`}
                              onChange={(e) => {
                                const hexColor = e.target.value;
                                setCustomThemeColors((s) => ({
                                  bgColor: s?.bgColor || 'FFFFFF',
                                  textColor: hexColor.substring(1),
                                }));
                              }}
                            />
                          </div>
                        </div>
                        <div className='flex flex-row-reverse items-center gap-3'>
                          <label
                            aria-description='Custom Background Color'
                            htmlFor='custom_bg'
                          >
                            Background
                          </label>
                          <div className='rounded border border-gray-400 p-1 pb-0'>
                            <input
                              type='color'
                              className='m-0 inline-block'
                              id='custom_bg'
                              value={`#${
                                customThemeColors?.bgColor || 'ffffff'
                              }`}
                              onChange={(e) => {
                                const hexColor = e.target.value;
                                setCustomThemeColors((s) => ({
                                  textColor: s?.textColor || '000000',
                                  bgColor: hexColor.substring(1),
                                }));
                              }}
                            />
                          </div>
                        </div>
                      </div>
                      <div className='flex gap-8'>
                        <>
                          <Button
                            variant='primary'
                            text='Set Theme'
                            className='px-4 py-2'
                            onClick={() => {
                              //Save
                              changeThemeHandler('custom');
                              setIsCustomisingTheme(false);
                            }}
                          />
                          <Button
                            variant='link'
                            text='Cancel'
                            className='text-red-600'
                            onClick={() => {
                              //Save
                              setIsCustomisingTheme(false);
                            }}
                          />
                        </>
                      </div>
                    </>
                  )}
                </div>
              </div>
            </div>
          </div>
        </MyTabPanel>
        <MyTabPanel id='special'>
          {/* Special customisation options */}
          {/* Currently, it contains an option to generate a Table of Contents */}
          <div className='flex flex-col space-y-4 divide-y divide-gray-300'>
            <div className='px-16 py-6'>
              <div className='flex items-center justify-between'>
                <InfoTooltip
                  position='bottom'
                  infoTip="The Table of Contents makes reading easier by organizing the document's structure visually. This helps them find information more easily, making the overall reading experience smoother."
                >
                  <p>Generate Table of Contents</p>
                </InfoTooltip>
                <MyToggle
                  ariaLabel='Generate Table of Contents'
                  checked={!!modificationsObj?.generateTOC}
                  onChange={(checked) =>
                    changeConfigHandler('generateTOC', checked)
                  }
                />
              </div>
            </div>
          </div>
        </MyTabPanel>
      </Tabs>
      {/* Button at the bottom to save the changes */}
      <div className='border bg-stone-50 px-16 py-5 text-right shadow-2xl shadow-black'>
        <Button
          className=' px-6 py-2 text-base'
          loading={configSaveLoading}
          text={'Save Changes'}
          onClick={() => {
            // Call the onConfigSave function with the current modifications
            onConfigSave(modificationsObj);
          }}
        />
      </div>
    </div>
  );
};

export default CustomisationPanel;
