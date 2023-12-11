import { FC } from 'react';
import {
  Label,
  Slider,
  SliderOutput,
  SliderThumb,
  SliderTrack,
} from 'react-aria-components';
import { MySliderProps } from './interfaces';

// MySlider component: A customizable slider component using react-aria-components.
// It accepts generic type T for supporting different types of number formats.
function MySlider<T extends number | number[]>({
  label,
  thumbLabels,
  outputValFormat,
  ...props
}: MySliderProps<T>) {
  return (
    <Slider {...props} className=''>
      <div className='mb-2 flex'>
        {/* Label for the slider */}
        <Label className='flex-1'>{label}</Label>
        {/* SliderOutput displays the current value of the slider.
             It uses a custom format if outputValFormat is provided. */}
        {outputValFormat ? (
          <SliderOutput>
            {({ state }) => <span>{outputValFormat(state.values[0])}</span>}
          </SliderOutput>
        ) : (
          <SliderOutput />
        )}
      </div>
      <SliderTrack className='relative h-7 w-full'>
        {({ state, isDisabled }) => {
          return (
            <>
              {/* Track of the slider */}
              <div
                className={`absolute top-[50%] h-1 w-full translate-y-[-50%] rounded-full ${
                  isDisabled ? 'bg-gray-300/80' : ''
                } bg-slate-400/70 `}
              />
              {/* Fill of the slider representing the current value */}
              <div
                className={`absolute top-[50%] h-1 translate-y-[-50%] rounded-full bg-yellow-600 ${
                  isDisabled ? 'bg-gray-300/80' : ''
                }`}
                style={{ width: state.getThumbPercent(0) * 100 + '%' }}
              />
              {/* Thumb of the slider, which can be dragged */}
              <SliderThumb className='top-[50%] h-5 w-5 rounded-full border border-solid border-yellow-700/75 bg-yellow-700 outline-none ring-yellow-700 ring-offset-1 ring-offset-slate-200 transition focus-visible:ring-2 disabled:border-gray-500 disabled:bg-gray-500 dragging:bg-yellow-950' />
            </>
          );
        }}
      </SliderTrack>
    </Slider>
  );
}

export default MySlider;
