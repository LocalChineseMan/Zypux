package com.google.common.collect;

import java.util.Iterator;

class null extends TransformedIterator<Table.Cell<R, C, V>, V> {
  null(Iterator<? extends Table.Cell<R, C, V>> x0) {
    super(x0);
  }
  
  V transform(Table.Cell<R, C, V> cell) {
    return cell.getValue();
  }
}
