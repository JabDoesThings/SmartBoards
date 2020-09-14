package jab.utils;

public class UIUtils {

  public static void calculatePosition(
      Anchor anchor,
      ValueType x,
      ValueType y,
      float[] parent_pos,
      float[] parent_size,
      float[] pos,
      float[] _pos,
      float[] _size) {
    // Calculate anchored position.
    switch (anchor) {
      case TOP_LEFT:
        calculatePositionTopLeft(x, y, parent_pos, parent_size, pos, _pos);
        break;
      case TOP:
        calculatePositionTop(x, y, parent_pos, parent_size, pos, _pos, _size);
        break;
      case TOP_RIGHT:
        calculatePositionTopRight(x, y, parent_pos, parent_size, pos, _pos, _size);
        break;
      case RIGHT:
        calculatePositionRight(x, y, parent_pos, parent_size, pos, _pos, _size);
        break;
      case BOTTOM_RIGHT:
        calculatePositionBottomRight(x, y, parent_pos, parent_size, pos, _pos, _size);
        break;
      case BOTTOM:
        calculatePositionBottom(x, y, parent_pos, parent_size, pos, _pos, _size);
        break;
      case BOTTOM_LEFT:
        calculatePositionBottomLeft(x, y, parent_pos, parent_size, pos, _pos, _size);
        break;
      case LEFT:
        calculatePositionLeft(x, y, parent_pos, parent_size, pos, _pos, _size);
        break;
      case CENTER:
        calculatePositionCenter(x, y, parent_pos, parent_size, pos, _pos, _size);
        break;
    }
  }

  public static void calculatePositionTopLeft(
      ValueType x,
      ValueType y,
      float[] parent_pos,
      float[] parent_size,
      float[] pos,
      float[] _pos) {
    _pos[0] = calculateAxis(x, parent_pos[0], parent_size[0], pos[0]);
    _pos[1] = calculateAxis(y, parent_pos[1], parent_size[1], pos[1]);
  }

  public static void calculatePositionTop(
      ValueType x,
      ValueType y,
      float[] parent_pos,
      float[] parent_size,
      float[] pos,
      float[] _pos,
      float[] _size) {
    _pos[0] = calculateCenter(x, parent_pos[0], parent_size[0], pos[0], _size[0]);
    _pos[1] = calculateAxis(y, parent_pos[1], parent_size[1], pos[1]);
  }

  public static void calculatePositionTopRight(
      ValueType x,
      ValueType y,
      float[] parent_pos,
      float[] parent_size,
      float[] pos,
      float[] _pos,
      float[] _size) {
    _pos[0] = calculateOpposite(x, parent_pos[0], parent_size[0], pos[0], _size[0]);
    _pos[1] = calculateAxis(y, parent_pos[1], parent_size[1], pos[1]);
  }

  public static void calculatePositionRight(
      ValueType x,
      ValueType y,
      float[] parent_pos,
      float[] parent_size,
      float[] pos,
      float[] _pos,
      float[] _size) {
    _pos[0] = calculateOpposite(x, parent_pos[0], parent_size[0], pos[0], _size[0]);
    _pos[1] = calculateCenter(y, parent_pos[1], parent_size[1], pos[1], _size[1]);
  }

  public static void calculatePositionBottomRight(
      ValueType x,
      ValueType y,
      float[] parent_pos,
      float[] parent_size,
      float[] pos,
      float[] _pos,
      float[] _size) {
    _pos[0] = calculateOpposite(x, parent_pos[0], parent_size[0], pos[0], _size[0]);
    _pos[1] = calculateOpposite(y, parent_pos[1], parent_size[1], pos[1], _size[1]);
  }

  public static void calculatePositionBottom(
      ValueType x,
      ValueType y,
      float[] parent_pos,
      float[] parent_size,
      float[] pos,
      float[] _pos,
      float[] _size) {
    _pos[0] = calculateCenter(x, parent_pos[0], parent_size[0], pos[0], _size[0]);
    _pos[1] = calculateOpposite(y, parent_pos[1], parent_size[1], pos[1], _size[1]);
  }

  public static void calculatePositionBottomLeft(
      ValueType x,
      ValueType y,
      float[] parent_pos,
      float[] parent_size,
      float[] pos,
      float[] _pos,
      float[] _size) {
    _pos[0] = calculateAxis(x, parent_pos[0], parent_size[0], pos[0]);
    _pos[1] = calculateOpposite(y, parent_pos[1], parent_size[1], pos[1], _size[1]);
  }

  public static void calculatePositionLeft(
      ValueType x,
      ValueType y,
      float[] parent_pos,
      float[] parent_size,
      float[] pos,
      float[] _pos,
      float[] _size) {
    _pos[0] = calculateAxis(x, parent_pos[0], parent_size[0], pos[0]);
    _pos[1] = calculateCenter(y, parent_pos[1], parent_size[1], pos[1], _size[1]);
  }

  public static void calculatePositionCenter(
      ValueType x,
      ValueType y,
      float[] parent_pos,
      float[] parent_size,
      float[] pos,
      float[] _pos,
      float[] _size) {
    _pos[0] = calculateCenter(x, parent_pos[0], parent_size[0], pos[0], _size[0]);
    _pos[1] = calculateCenter(y, parent_pos[1], parent_size[1], pos[1], _size[1]);
  }

  public static void calculateSize(
      ValueType w, ValueType h, float[] parent_size, float[] size, float[] _size) {
    _size[0] = w == ValueType.PIXEL ? _size[0] = size[0] : parent_size[0] * size[0];
    _size[1] = h == ValueType.PIXEL ? _size[1] = size[1] : parent_size[1] * size[1];
  }

  public static float calculateAxis(
      ValueType type, float parent_pos, float parent_size, float pos) {
    if (type == ValueType.PIXEL) {
      return parent_pos + pos;
    } else {
      return parent_pos + (parent_size * pos);
    }
  }

  public static float calculateCenter(
      ValueType type, float parent_pos, float parent_size, float pos, float _size) {
    if (type == ValueType.PIXEL) {
      return parent_pos + (parent_size / 2.0F) - (_size / 2.0F) + pos;
    } else {
      return parent_pos + (parent_size / 2.0F) - (_size / 2.0F) + (pos * parent_size);
    }
  }

  public static float calculateOpposite(
      ValueType type, float parent_pos, float parent_size, float pos, float _size) {
    if (type == ValueType.PIXEL) {
      return ((parent_pos + parent_size) - _size) + pos;
    } else {
      // x = parent_x - element_width + (parent_width * percentage_x)
      return ((parent_pos + parent_size) - _size) + (parent_size * pos);
    }
  }
}
