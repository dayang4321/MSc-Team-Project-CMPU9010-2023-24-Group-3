import Head from 'next/head';
import React, { useContext, useState } from 'react';
import DefaultLayout from '../layouts/DefaultLayout';
import Button from '../components/UI/Button';
import MyToggle from '../components/UI/MyToggle';
import { useRouter } from 'next/router';
import axiosInit from '../services/axios';
import InfoTooltip from '../components/InfoTooltip/InfoTooltip';
import { reportException } from '../services/errorReporting';
import { ToastQueue } from '@react-spectrum/toast';
import { AuthContext } from '../contexts/AuthContext';

// Define the type for the primary fixes that can be applied to a document
type PrimaryFix =
  | 'fontStyle'
  | 'fontSize'
  | 'interSpacing'
  | 'lineSpacing'
  | 'contrast'
  | 'italics'
  | 'alignment';

// Define the main functional component for reviewing accessibility settings
export default function AccessibilityReview() {
  // React hooks for updating the loading states of document modification
  const [isModifyLoading, setIsModifyLoading] = useState(false);
  const router = useRouter();
  const { doc_id, doc_key, version_id } = router.query;

  // Use context to access the authenticated user's information
  const { user } = useContext(AuthContext);

  // Retrieve the saved user presets if it's available
  const currentUserPresets = user?.userPresets;

  //Managing the choices regarding document modification
  const [choicesObj, setChoicesObj] = useState<Record<PrimaryFix, boolean>>(
    user
      ? {
          // Initialize state with user presets if available
          fontStyle: !!user?.userPresets?.fontType,
          fontSize: !!user?.userPresets?.fontSize,
          interSpacing: !!user?.userPresets?.characterSpacing,
          lineSpacing: !!user?.userPresets?.lineSpacing,
          contrast: !!(
            user?.userPresets?.backgroundColor ||
            user?.userPresets?.backgroundColor
          ),
          italics: !!user?.userPresets?.removeItalics,
          alignment: !!user?.userPresets?.alignment,
        }
      : {
          // Default settings if no user presets are available
          fontStyle: true,
          fontSize: true,
          interSpacing: true,
          lineSpacing: true,
          contrast: true,
          italics: true,
          alignment: true,
        }
  );

  // Set the toggle choice handler function
  const setChoiceHandler = (choice: PrimaryFix) => {
    setChoicesObj((s) => ({
      ...s,
      [choice]: !s[choice],
    }));
  };

  // Defining the functional component: OnReviewConfirm, to handle for confirming review changes
  const onReviewConfirm = () => {
    // Set the loading state to true
    setIsModifyLoading(true);

    // Define the DocModifyParams which indicate the different document modification parameters
    const docModParams: Partial<DocModifyParams> = {
      fontType: choicesObj.fontStyle
        ? currentUserPresets?.fontType || 'arial'
        : undefined,
      fontSize: choicesObj.fontSize
        ? currentUserPresets?.fontSize || 12
        : undefined,
      lineSpacing: choicesObj.lineSpacing
        ? currentUserPresets?.lineSpacing || 1.5
        : undefined,
      fontColor: choicesObj.contrast
        ? currentUserPresets?.fontColor || '000000'
        : undefined,
      backgroundColor: choicesObj.contrast
        ? currentUserPresets?.backgroundColor || 'FFFFFF'
        : undefined,
      characterSpacing: choicesObj.interSpacing
        ? currentUserPresets?.characterSpacing || 2.5
        : undefined,
      removeItalics: choicesObj.italics
        ? currentUserPresets?.removeItalics || true
        : undefined,
      alignment: choicesObj.alignment
        ? currentUserPresets?.alignment || 'LEFT'
        : undefined,
    };

    // Make a POST request to the modifyFile API endpoint
    axiosInit
      .post<DocumentData>(
        '/api/file/modifyFile',
        { ...docModParams },
        {
          params: {
            filename: doc_key,
            docID: doc_id,
            versionID: version_id,
          },
        }
      )
      .then((res) => {
        // On successful response, navigate to the reader page with the updated document
        // console.log(res);
        router.push({
          pathname: '/reader',
          query: {
            doc_id: res.data.documentID,
          },
        });
      })
      .catch((err) => {
        // On error, display a toast message and log the exception
        // console.log(err);
        ToastQueue.negative(
          `An error occurred! ${
            err?.response?.data.detail || err?.message || ''
          }`,
          {
            timeout: 5000,
          }
        );
        reportException(err, {
          category: 'modify',
          message: 'Failed to modify document',
          data: {
            origin: 'Review Screen',
          },
        });
      })
      .finally(() => {
        // Reset the loading state after receiving the data or handling an error if the API fails
        setIsModifyLoading(false);
      });
  };

  // Render the component UI for the accessibility review page
  return (
    <DefaultLayout>
      <Head>
        <title>Accessibilator | Review your modifications</title>
        <link rel='icon' href='/favicon.ico' />
      </Head>

      {/* Main content area */}
      <main className='flex flex-1 flex-col items-center justify-center bg-slate-50 py-14 pb-8 text-center text-base text-gray-900'>
        {/* Accessibility review section */}
        <div className='text-center'>
          <h1 className='mb-7 text-4xl font-bold'>
            The document has been uploaded successfully
          </h1>
          <h3 className='text-2xl'>
            Here are some changes suggested to make your document easier to read
          </h3>

          {/* Options for modifying the document's accessibility settings */}
          <div className='mx-auto mt-12 max-w-[50rem] divide-y divide-solid rounded-md border border-gray-600 p-7'>
            {/* Toggle for font size increase */}
            <div className='flex items-center justify-between py-5'>
              <InfoTooltip infoTip='Larger font sizes (12-14 pt.) makes reading easier, especially for readers who may find smaller text challenging to follow'>
                <p className='text-lg'>Increase font size</p>
              </InfoTooltip>
              <MyToggle
                ariaLabel='Font size increased'
                checked={choicesObj.fontSize}
                onChange={() => setChoiceHandler('fontSize')}
              />
            </div>
            <div className='flex items-center justify-between py-5'>
              <InfoTooltip infoTip='We use sans serif fonts like Arial as they appear less crowded, making each letter more distinct and easier to read for people living with dyslexia.'>
                <p className='text-lg'>Change font style</p>
              </InfoTooltip>

              <MyToggle
                ariaLabel='Font style changed'
                checked={choicesObj.fontStyle}
                onChange={() => setChoiceHandler('fontStyle')}
              />
            </div>
            <div className='flex items-center justify-between py-5'>
              <InfoTooltip infoTip='Increasing the space between letters (around 35% of the average letter width) leads to a more readable text by reducing visual crowding, a common issue for those with dyslexia.'>
                <p className='text-lg'>Increase letter spacing</p>
              </InfoTooltip>
              <MyToggle
                ariaLabel='Inter-letter spacing increased'
                checked={choicesObj.interSpacing}
                onChange={() => setChoiceHandler('interSpacing')}
              />
            </div>
            <div className='flex items-center justify-between py-5'>
              <InfoTooltip infoTip='We try to avoid italics as much as possible, as they can cause letters to appear connected and crowded, which can be challenging for readers with dyslexia. Bold text will used for emphasis instead'>
                <p className='text-lg'>Remove Italics</p>
              </InfoTooltip>
              <MyToggle
                ariaLabel='Italics removed'
                checked={choicesObj.italics}
                onChange={() => setChoiceHandler('italics')}
              />
            </div>
            <div className='flex items-center justify-between py-5'>
              <InfoTooltip infoTip='The recommended line spacing (1.5) improves text clarity and reduces visual stress, making it easier for readers to follow lines of text.'>
                <p className='text-lg'>Increase line spacing</p>
              </InfoTooltip>
              <MyToggle
                ariaLabel='Line spacing increased'
                checked={choicesObj.lineSpacing}
                onChange={() => setChoiceHandler('lineSpacing')}
              />
            </div>

            <div className='flex items-center justify-between py-5'>
              <InfoTooltip infoTip='High contrast between text and background minimizes visual strain and enhances visibility of characters, particularly important for individuals with reading and/or visual difficulties'>
                <p className='text-lg'>Improve Contrast</p>
              </InfoTooltip>
              <MyToggle
                ariaLabel='Contrast increased'
                checked={choicesObj.contrast}
                onChange={() => setChoiceHandler('contrast')}
              />
            </div>
            <div className='flex items-center justify-between py-5'>
              <InfoTooltip infoTip="We've aligned the text to the left without justification. This alignment helps in maintaining a consistent visual flow, making it easier to find the start and finish of each line. It also ensures even spacing between words.">
                <p className='text-lg'>Left Align</p>
              </InfoTooltip>
              <MyToggle
                ariaLabel='Alignment changed'
                checked={choicesObj.alignment}
                onChange={() => setChoiceHandler('alignment')}
              />
            </div>
          </div>
        </div>

        {/* Button section for navigation */}
        <div className='mt-8 w-full max-w-4xl text-right '>
          {/* Back button */}
          <Button
            variant='link'
            className=' mr-10 border border-yellow-900 px-6 py-2 text-base font-medium'
            text={'Back'}
            onClick={() => {
              router.back();
            }}
          />
          {/* Continue button */}
          <Button
            className=' px-6 py-2 text-base'
            loading={isModifyLoading}
            text={'Continue'}
            onClick={() => {
              onReviewConfirm();
            }}
          />
        </div>
      </main>
    </DefaultLayout>
  );
}
