// Importing necessary React and Next.js components
import React, { useContext, useEffect, useState } from 'react';
import DefaultLayout from '../../layouts/DefaultLayout';
import Head from 'next/head';
import Image from 'next/image';
import { FONT_STYLE_OPTIONS } from '../../configs/selectOptions';
import Button from '../../components/UI/Button';
import SlideModal from '../../components/UI/SlideModal';
import CustomisationPanel from '../../components/CustomisationPanel/CustomisationPanel';
import { AuthContext } from '../../contexts/AuthContext';
import IsProtectedRoute from '../../hoc/IsProtectedRoute';
import axiosInit from '../../services/axios';
import { ToastQueue } from '@react-spectrum/toast';
import { reportException } from '../../services/errorReporting';

// Default settings for document modification parameters
const defaultSettings: DocModifyParams = {
  fontType: 'arial',
  fontSize: 12,
  lineSpacing: 1.5,
  fontColor: '000000',
  backgroundColor: 'FFFFFF',
  characterSpacing: 2.5,
  removeItalics: true,
  alignment: 'LEFT',
  generateTOC: false,
  borderGeneration: false,
  headerGeneration: false,
  paragraphSplitting: false,
  syllableSplitting: false,
  handlePunctuations: false,
};

const PresetsPage = () => {
  // Using the context Object to access the authentication-related data
  const { user, logout, fetchUser, token } = useContext(AuthContext);

  // Define the state hooks to manage user settings and modal visibility
  const [userSettings, setUserSettings] = useState({
    ...defaultSettings,
  });
  const [slideModalOpen, setSlideModalOpen] = useState(false);
  const [isUserPresetLoading, setIsUserPresetLoading] = useState(true);

  /**
   * Define the async function for fetching user's preset configurations
   * @returns The user pre-defined settings
   */
  const fetchUserPresets = async () => {
    setIsUserPresetLoading(true);
    try {
      const fetchUserRes = await axiosInit.get<{ user: User | null }>(
        '/api/user/me'
      );
      const currUserPresets = fetchUserRes?.data?.user?.userPresets;

      setIsUserPresetLoading(false);
      return Promise.resolve(currUserPresets);
    } catch (err) {
      ToastQueue.negative(
        `Failed to load settings! ${
          err?.response?.data.detail || err?.message || ''
        }`,
        {
          timeout: 3000,
        }
      );
      setIsUserPresetLoading(false);
      return Promise.reject(err);
    }
  };

  // Define the useEffect hook to fetch the user presets on component mount
  useEffect(() => {
    fetchUserPresets().then((value) => {
      !!value && setUserSettings((state) => ({ ...state, ...value }));
    });
  }, []);

  // Define the function for handling the save action of user configurations
  const onSaveConfig = (userPresetData: DocModifyParams) => {
    setIsUserPresetLoading(true);

    axiosInit
      .post<DocumentData>('/api/user/presets', {
        ...userPresetData,
        characterSpacing: !!userPresetData.characterSpacing
          ? userPresetData?.characterSpacing * 10
          : userPresetData?.characterSpacing,
      })
      .then((res) => {
        setUserSettings({
          ...userPresetData,
          characterSpacing: !!userPresetData.characterSpacing
            ? userPresetData?.characterSpacing * 10
            : userPresetData?.characterSpacing,
        }),
          ToastQueue.positive('Settings saved successfully', {
            timeout: 3000,
          });
        fetchUser(user);
        setSlideModalOpen(false);
      })
      .catch((err) => {
        // console.log(err);
        ToastQueue.negative(
          `An error occurred! ${
            err?.response?.data.detail || err?.message || ''
          }`,
          {
            timeout: 3000,
          }
        );
        reportException(err, {
          category: 'presets',
          message: 'Failed to set user presets',
          data: {
            origin: 'Presets Screen',
          },
        });
      })
      .finally(() => {
        setIsUserPresetLoading(false);
      });
  };

  // Parsing the user settings Object to display as preset options
  const presetsArr = [
    `Font Type: ${FONT_STYLE_OPTIONS.find(
      (opt) => opt.id === userSettings.fontType
    )?.name}`,
    `Font Size: ${userSettings.fontSize}px`,
    `Line Spacing: ${userSettings.lineSpacing}`,
    `Letter Spacing: ${
      userSettings.characterSpacing
        ? `${userSettings.characterSpacing * 10}%`
        : ''
    }`,
    `Remove Italics: ${userSettings.removeItalics ? 'Yes' : 'No'}`,
    `Align Text: ${userSettings?.alignment?.toLowerCase()}`,
  ];

  return (
    <>
      <DefaultLayout>
        <Head>
          <title>Accessibilator | My Settings</title>
          <link rel='icon' href='/favicon.ico' />
        </Head>
        <main className='flex flex-1 flex-col justify-center py-16  pb-8 text-center text-base text-gray-900'>
          <div className='grid flex-1 grid-cols-4 gap-8 px-16'>
            <div className='col-span-1 flex flex-col items-center rounded border border-gray-400/60 bg-stone-50 p-10'>
              <div>
                <Image
                  width={256}
                  height={256}
                  src={`https://ui-avatars.com/api/?name=${user?.username}&size=256&length=1&bold=true`}
                  className='rounded-full '
                  alt='profile name initials'
                />
              </div>
              <p className='mt-8 text-lg font-semibold'>{user?.username}</p>
              <p className='mt-3 font-medium'> {user?.email}</p>

              <Button
                text='Logout'
                variant='link'
                className='btn-link mt-auto'
                onClick={() => {
                  logout();
                }}
              />
            </div>

            <div className='col-span-3 flex flex-col rounded border border-gray-400/60 bg-stone-50 px-16 py-10 text-left'>
              <h2 className='text-3xl font-medium'>My Settings</h2>

              <div className='mt-10 flex flex-wrap gap-x-5 gap-y-6 '>
                {presetsArr.map((val, idx) => {
                  return (
                    <div
                      key={idx}
                      className='inline-block rounded-md border-2 border-dashed border-yellow-900 px-5 py-2 text-lg capitalize'
                    >
                      {val}
                    </div>
                  );
                })}
              </div>
              <div className='mt-6 inline-flex h-36 w-24 flex-col items-stretch  rounded-md border-2 border-dashed border-yellow-900 p-2 text-lg capitalize'>
                <div
                  className='flex flex-1 flex-col items-center justify-center border border-gray-200 shadow-md'
                  style={{
                    backgroundColor: `#${userSettings.backgroundColor}`,
                    color: `#${userSettings.fontColor}`,
                  }}
                  key={'theme'}
                >
                  <span className='inline-block text-xl font-semibold'>Aa</span>
                  <span className='mt-1 inline-block text-sm'>Theme</span>
                </div>
              </div>

              <div className='mt-auto'>
                <Button
                  className='px-6 py-2 text-base'
                  text={'Modify Settings'}
                  onClick={() => {
                    setSlideModalOpen(true);
                  }}
                />
              </div>
            </div>
          </div>
        </main>
      </DefaultLayout>
      {/* Slide Modal for customisation panel */}
      <SlideModal
        open={slideModalOpen}
        setOpen={setSlideModalOpen}
        title={'Customisation Panel'}
      >
        {/* Conditional rendering of Customisation Panel */}
        {userSettings && (
          <CustomisationPanel
            onConfigSave={onSaveConfig}
            configSaveLoading={isUserPresetLoading}
            customisationConfig={userSettings}
          />
        )}
      </SlideModal>
    </>
  );
};

// Wrapping the component with a higher-order component for route protection
export default IsProtectedRoute(PresetsPage);
