import Head from 'next/head';
import React, { useState } from 'react';
import DefaultLayout from '../layouts/DefaultLayout';
import Button from '../components/UI/Button';
import MyToggle from '../components/UI/MyToggle';
import { useRouter } from 'next/router';
import axiosInit from '../services/axios';
import InfoTooltip from '../components/InfoTooltip/InfoTooltip';
import { reportException } from '../services/errorReporting';
import { ToastQueue } from '@react-spectrum/toast';

type PrimaryFix =
  | 'fontStyle'
  | 'fontSize'
  | 'interSpacing'
  | 'lineSpacing'
  | 'contrast'
  | 'italics'
  | 'alignment';

export default function AccessibilityReview() {
  // const [showMessage, setShowMessage] = useState(false);

  const [isModifyLoading, setIsModifyLoading] = useState(false);
  const router = useRouter();
  const { doc_id, doc_key, version_id } = router.query;

  const [choicesObj, setChoicesObj] = useState<Record<PrimaryFix, boolean>>({
    fontStyle: true,
    fontSize: true,
    interSpacing: true,
    lineSpacing: true,
    contrast: true,
    italics: true,
    alignment: true,
  });

  const setChoiceHandler = (choice: PrimaryFix) => {
    setChoicesObj((s) => ({
      ...s,
      [choice]: !s[choice],
    }));
  };

  const onReviewConfirm = () => {
    setIsModifyLoading(true);

    const docModParams: Partial<DocModifyParams> = {
      fontType: choicesObj.fontStyle ? 'arial' : undefined,
      fontSize: choicesObj.fontSize ? 12 : undefined,
      lineSpacing: choicesObj.lineSpacing ? 1.5 : undefined,
      fontColor: choicesObj.contrast ? '000000' : undefined,
      backgroundColor: choicesObj.contrast ? 'FFFFFF' : undefined,
      characterSpacing: choicesObj.interSpacing ? 2.5 : undefined,
      removeItalics: choicesObj.italics ? true : undefined,
      alignment: choicesObj.alignment ? 'LEFT' : undefined,
    };

    axiosInit
      .get<DocumentData>('/api/file/modifyFile', {
        params: {
          filename: doc_key,
          docID: doc_id,
          versionID: version_id,
          ...docModParams,
        },
      })
      .then((res) => {
        //  console.log(res);
        router.push({
          pathname: '/reader',
          query: {
            doc_id: res.data.documentID,
          },
        });
      })
      .catch((err) => {
        //  console.log(err);
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
        setIsModifyLoading(false);
      });
  };

  return (
    <DefaultLayout>
      <Head>
        <title>Accessibilator</title>
        <link rel='icon' href='/favicon.ico' />
      </Head>

      <main className='flex flex-1 flex-col items-center justify-center bg-slate-50 py-16 pb-8 text-center text-gray-900'>
        <div className='max-w-[50rem] text-center'>
          <h1 className='mb-11  text-4xl font-bold'>
            The document you uploaded has been processed and modified
          </h1>
          <h3 className='text-2xl'>
            Here are few modifications we&apos;ve made to the document to make
            it more readable and accessible
          </h3>

          <div className='mt-12 divide-y divide-solid rounded-md border border-gray-600 p-7'>
            <div className='flex items-center justify-between py-5'>
              <InfoTooltip infoTip='We use sans serif fonts like Arial or Comic Sans as they appear less crowded, making each letter more distinct and easier to read for people living with dyslexia.'>
                <p>Font style changed</p>
              </InfoTooltip>

              <MyToggle
                ariaLabel='Font style changed'
                checked={choicesObj.fontStyle}
                onChange={() => setChoiceHandler('fontStyle')}
              />
            </div>
            <div className='flex items-center justify-between py-5'>
              <InfoTooltip infoTip='Larger font sizes (12-14 pt.) makes reading easier, especially for readers who may find smaller text challenging to follow'>
                <p>Font size increased</p>
              </InfoTooltip>
              <MyToggle
                ariaLabel='Font size increased'
                checked={choicesObj.fontSize}
                onChange={() => setChoiceHandler('fontSize')}
              />
            </div>
            <div className='flex items-center justify-between py-5'>
              <InfoTooltip infoTip='Increasing the space between letters (around 35% of the average letter width) leads to a more readable text by reducing visual crowding, a common issue for those with dyslexia.'>
                <p>Letter spacing increased</p>
              </InfoTooltip>
              <MyToggle
                ariaLabel='Inter-letter spacing increased'
                checked={choicesObj.interSpacing}
                onChange={() => setChoiceHandler('interSpacing')}
              />
            </div>
            <div className='flex items-center justify-between py-5'>
              <InfoTooltip infoTip='The recommended line spacing (1.5) improves text clarity and reduces visual stress, making it easier for readers to follow lines of text.'>
                <p>Line spacing increased</p>
              </InfoTooltip>
              <MyToggle
                ariaLabel='Line spacing increased'
                checked={choicesObj.lineSpacing}
                onChange={() => setChoiceHandler('lineSpacing')}
              />
            </div>
            <div className='flex items-center justify-between py-5'>
              <InfoTooltip infoTip='We try to avoid italics as much as possible, as they can cause letters to appear connected and crowded, which can be challenging for readers with dyslexia. Bold text will used for emphasis instead'>
                <p>Italics removed</p>
              </InfoTooltip>
              <MyToggle
                ariaLabel='Italics removed'
                checked={choicesObj.italics}
                onChange={() => setChoiceHandler('italics')}
              />
            </div>
            <div className='flex items-center justify-between py-5'>
              <InfoTooltip infoTip='High contrast between text and background minimizes visual strain and enhances visibility of characters, particularly important for individuals with reading and/or visual difficulties'>
                <p>Contrast increased</p>
              </InfoTooltip>
              <MyToggle
                ariaLabel='Contrast increased'
                checked={choicesObj.contrast}
                onChange={() => setChoiceHandler('contrast')}
              />
            </div>
            <div className='flex items-center justify-between py-5'>
              <InfoTooltip infoTip="We've aligned the text to the left without justification. This alignment helps in maintaining a consistent visual flow, making it easier to find the start and finish of each line. It also ensures even spacing between words.">
                <p>Alignment changed</p>
              </InfoTooltip>
              <MyToggle
                ariaLabel='Alignment changed'
                checked={choicesObj.alignment}
                onChange={() => setChoiceHandler('alignment')}
              />
            </div>
          </div>
        </div>

        <div className='mt-8 w-full max-w-4xl text-right '>
          <Button
            variant='link'
            className=' mr-10 border border-yellow-900 px-6 py-2 text-base font-medium'
            text={'Back'}
            onClick={() => {
              router.back();
            }}
          />
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
